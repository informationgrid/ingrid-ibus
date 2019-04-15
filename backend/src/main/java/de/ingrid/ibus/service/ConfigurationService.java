/*-
 * **************************************************-
 * ingrid-ibus-backend
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.ibus.service;

import de.ingrid.codelists.CodeListService;
import de.ingrid.codelists.comm.HttpCLCommunication;
import de.ingrid.codelists.model.CodeList;
import de.ingrid.elasticsearch.ElasticsearchNodeFactoryBean;
import de.ingrid.ibus.WebSecurityConfig;
import de.ingrid.ibus.comm.BusServer;
import de.ingrid.ibus.config.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.DefaultPropertiesPersister;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class ConfigurationService {

    private static Logger log = LogManager.getLogger(ConfigurationService.class);
    private boolean needPasswordChange;

    @Value("${app.version}")
    private String appVersion;

    @Value("${app.timestamp}")
    private String appTimestamp;

    private File settingsFile;

    private final CodeListService codeListService;

    private final ElasticsearchNodeFactoryBean elasticsearchBean;

    private final WebSecurityConfig webSecurityConfig;

    private final BusServer busServer;

    private final CodelistConfiguration codelistConfiguration;

    private final IBusConfiguration busConfiguration;

    private final ElasticsearchConfiguration elasticConfiguration;

    private final SecurityProperties springConfiguration;

    private final ServerProperties serverConfiguration;

    private final IndicesService indicesService;

    private Properties propertiesSystem;
    private Properties properties;

    private static String[] configurableProps = new String[]{
            "codelistrepo.url", "codelistrepo.username", "elastic.remoteHosts", "ibus.url", "ibus.port"
    };

    @Autowired
    public ConfigurationService(CodeListService codeListService, ElasticsearchNodeFactoryBean elasticsearchBean, WebSecurityConfig webSecurityConfig, BusServer busServer, CodelistConfiguration codelistConfiguration, IBusConfiguration busConfiguration, ElasticsearchConfiguration elasticConfiguration, SecurityProperties securityConfiguration, ServerProperties serverConfiguration, IndicesService indicesService) throws IOException {
        ClassPathResource ibusSystemConfig = new ClassPathResource("/application.properties");
        ClassPathResource ibusConfig = new ClassPathResource("/application-default.properties");

        if (ibusConfig.exists()) {
            this.settingsFile = ibusConfig.getFile();
        } else {
            Path path = Paths.get("conf", "application-default.properties");
            if (path.toFile().exists()) {
                this.settingsFile = path.toFile();
            } else {
                this.settingsFile = Files.createFile(path).toFile();
            }
        }

        // create a sorted properties file
        propertiesSystem = new Properties();
        propertiesSystem.load(new FileReader(ibusSystemConfig.getFile()));

        Properties propertiesOverride = new Properties();
        propertiesOverride.load(new FileReader(this.settingsFile));

        // if no password has been set in override configuration or set through environment variable,
        // then we need to change it and tell frontend
        this.needPasswordChange =
                propertiesOverride.get("spring.security.user.password") == null
                && System.getenv("IBUS_PASSWORD") == null;

        // TODO: Refactor to let beans reconfigure themselves by implementing same interface
        this.properties = new Properties();
        this.properties.putAll(propertiesSystem);
        this.properties.putAll(propertiesOverride);
        this.codeListService = codeListService;
        this.elasticsearchBean = elasticsearchBean;
        this.webSecurityConfig = webSecurityConfig;
        this.busServer = busServer;
        this.codelistConfiguration = codelistConfiguration;
        this.busConfiguration = busConfiguration;
        this.elasticConfiguration = elasticConfiguration;
        this.springConfiguration = securityConfiguration;
        this.serverConfiguration = serverConfiguration;
        this.indicesService = indicesService;
    }

    @PostConstruct
    public void init() {
        // use resolved properties values (instead of ${xxx:yyy})
        this.properties.put("codelistrepo.url", codelistConfiguration.getUrl());
        this.properties.put("codelistrepo.username", codelistConfiguration.getUsername());
        this.properties.put("codelistrepo.password", codelistConfiguration.getPassword());
        this.properties.put("elastic.remoteHosts", String.join(",", elasticConfiguration.getRemoteHosts()));
        this.properties.put("server.port", String.valueOf( serverConfiguration.getPort() ));
        this.properties.put("ibus.url", busConfiguration.getUrl());
        this.properties.put("spring.security.user.password", springConfiguration.getUser().getPassword());

    }

    public boolean writeConfiguration(Properties configuration) throws Exception {
        DefaultPropertiesPersister p = new DefaultPropertiesPersister();

        Properties modifiedProperties = getModifiedProperties(configuration);

        this.properties = new Properties();
        this.properties.putAll(propertiesSystem);
        this.properties.putAll(modifiedProperties);

        OutputStream out = null;
        try {
            out = new FileOutputStream(settingsFile);
            p.store(modifiedProperties, out, "Written by ConfigurationService");
        } catch (Exception e) {
            log.error("Error writing configuration", e);
            throw e;
        } finally {
            try {
                if (out != null) out.close();
            } catch (IOException e) {
                log.error("Error closing configuration file", e);
            }
        }

        // map configuration to ConfigBean
        boolean ibusChanged = !busConfiguration.getUrl().equals(configuration.get("ibus.url")) || (busConfiguration.getPort() != Integer.valueOf((String) configuration.get("ibus.port")));
        busConfiguration.setUrl( (String) configuration.get("ibus.url") );
        busConfiguration.setPort( Integer.valueOf((String) configuration.get("ibus.port")) );
        codelistConfiguration.setUrl( (String) configuration.get("codelistrepo.url") );
        codelistConfiguration.setUsername( (String) configuration.get("codelistrepo.username") );

        updateBeansConfiguration(configuration, ibusChanged);

        // check if elasticsearch connection was established the first time and needs index "ingrid_meta"
        try {
            this.indicesService.prepareIndices();
        } catch (NoNodeAvailableException e) {
            log.warn("Elasticsearch does not seem to be configured, since we could not connect to it. Please check the settings.");
        }

        return true;
    }

    private void updateBeansConfiguration(Properties configuration, boolean ibusChanged) throws Exception {
        // update codelist repository connection
        HttpCLCommunication communication = new HttpCLCommunication();
        communication.setRequestUrl(configuration.get("codelistrepo.url") + "/rest/getCodelists");
        communication.setUsername((String) configuration.get("codelistrepo.username"));

        // only update password if new one was set, otherwise use the old one
        if (configuration.get("codelistrepo.password") != null) {
            codelistConfiguration.setPassword( (String) configuration.get("codelistrepo.password") );
        }
        communication.setPassword(codelistConfiguration.getPassword());
        codeListService.setComm(communication);

        // Elasticsearch
        String remoteHosts = (String) configuration.get("elastic.remoteHosts");
        if (remoteHosts != null) {
            try {
                String[] remoteHostsArray;
                if ("".equals(remoteHosts)) {
                    remoteHostsArray = new String[0];
                } else {
                    remoteHostsArray = remoteHosts.split(",");
                }

                elasticConfiguration.setRemoteHosts( remoteHostsArray );
                elasticsearchBean.createTransportClient(remoteHostsArray);
            } catch (UnknownHostException e) {
                log.error("Error updating elasticsearch connection", e);
            }
        }

        // Admin Password
        String adminPassword = (String) configuration.get("spring.security.user.password");
        if (adminPassword != null && !"".equals(adminPassword)) {
            webSecurityConfig.secureWebapp(adminPassword);
            springConfiguration.getUser().setPassword( adminPassword );
            needPasswordChange = false;
        }

        // iBus
        if (ibusChanged) {
            busServer.configureIBus();
        }
    }

    /**
     * Return only those properties that differ from the system properties. This way
     * we only store the changed properties into the override configuration.
     *
     * @return a property list with those items different from system behaviour
     */
    private Properties getModifiedProperties(Properties otherProps) {
        Properties modProps = new Properties() {
            @Override
            public synchronized Enumeration<Object> keys() {
                return Collections.enumeration(new TreeSet<>(super.keySet()));
            }
        };

        Enumeration<Object> propKeys = this.properties.keys();
        while (propKeys.hasMoreElements()) {
            String key = (String) propKeys.nextElement();
            boolean isJustOverridden = otherProps.containsKey(key);
            boolean modIsDifferentFromSystem = isJustOverridden && !(otherProps.get(key).equals(this.propertiesSystem.get(key)));
            boolean userIsDifferentFromSystem = !this.properties.get(key).equals(this.propertiesSystem.get(key));
            if (modIsDifferentFromSystem) {
                modProps.put(key, otherProps.get(key));
            } else if (!isJustOverridden && userIsDifferentFromSystem) {
                modProps.put(key, this.properties.get(key));
            }
        }

        return modProps;
    }

    public Properties getConfiguration() {
        Properties sparseConfig = new Properties();
        sparseConfig.put("version", appVersion);
        sparseConfig.put("timestamp", appTimestamp);
        sparseConfig.put("needPasswordChange", String.valueOf(this.needPasswordChange));

        for (String key : configurableProps) {
            sparseConfig.put(key, properties.getProperty(key));
        }

        return sparseConfig;
    }

    public Properties getStatus() {
        Properties props = new Properties();

        List<CodeList> codeLists = codeListService.updateFromServer(new Date().getTime());
        props.put("codelistrepo", codeLists == null ? "false" : "true");
        props.put("elasticsearch", ((TransportClient)elasticsearchBean.getClient()).connectedNodes().size() > 0 ? "true" : "false");

        return props;
    }

}
