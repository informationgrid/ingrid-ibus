/*-
 * **************************************************-
 * ingrid-ibus-backend
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
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
import de.ingrid.elasticsearch.ElasticConfig;
import de.ingrid.elasticsearch.ElasticsearchNodeFactoryBean;
import de.ingrid.elasticsearch.IndexManager;
import de.ingrid.ibus.WebSecurityConfig;
import de.ingrid.ibus.comm.BusServer;
import de.ingrid.ibus.config.*;
import javax.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.DefaultPropertiesPersister;

import java.io.*;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

class ElasticConnectionCheck extends Thread {

    private static Logger log = LogManager.getLogger(ElasticConnectionCheck.class);

    private final ElasticsearchNodeFactoryBean elasticsearchBean;
    private final ElasticConfig elasticConfig;
    private final IndexManager indexManager;
    private final IndicesService indicesService;

    ElasticConnectionCheck(ElasticsearchNodeFactoryBean elasticsearchBean, ElasticConfig elasticConfig, IndexManager indexManager, IndicesService indicesService) {
        this.elasticsearchBean = elasticsearchBean;
        this.elasticConfig = elasticConfig;
        this.indexManager = indexManager;
        this.indicesService = indicesService;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(10000);
                log.debug("Checking Elasticsearch connection");
                int connectedNodes = ((TransportClient)elasticsearchBean.getClient()).connectedNodes().size();
                if (connectedNodes == 0) {
                    log.info("Elasticsearch not connected ... Reconnecting");
                    elasticsearchBean.createTransportClient(elasticConfig);
                    indexManager.init();
                    indicesService.init();
                }
            } catch (InterruptedException e) {
                log.info("Thread 'ElasticConnectionCheck' interrupted");
                throw new RuntimeException(e);
            } catch (UnknownHostException e) {
                log.debug("Connection could not be established: Unknown host " + String.join(",", elasticConfig.remoteHosts));
            } catch (Exception e) {
                log.error("An exception occurred while checking Elasticsearch connection", e.getMessage());
            }
        }
    }
}

@Service
public class ConfigurationService {

    private static Logger log = LogManager.getLogger(ConfigurationService.class);
    private boolean needPasswordChange;

    @Value("${app.version}")
    private String appVersion;

    @Value("${app.timestamp}")
    private String appTimestamp;

    private final ElasticConfig elasticConfig;

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

    private final IndexManager indexManager;

    private Properties propertiesSystem;
    private Properties properties;
    volatile private Thread elasticCheck;

    private static String[] configurableProps = new String[]{
            "codelistrepo.url", "codelistrepo.username", "elastic.remoteHosts", "elastic.username", "elastic.password", "elastic.sslTransport", "ibus.url", "ibus.port"
    };

    public ConfigurationService(CodeListService codeListService, ElasticsearchNodeFactoryBean elasticsearchBean, WebSecurityConfig webSecurityConfig, BusServer busServer, CodelistConfiguration codelistConfiguration, IBusConfiguration busConfiguration, ElasticsearchConfiguration elasticConfiguration, SecurityProperties securityConfiguration, ServerProperties serverConfiguration, IndicesService indicesService, ElasticConfig elasticConfig, IndexManager indexManager) throws IOException {
        ClassPathResource ibusSystemConfig = new ClassPathResource("/application.properties");
        ClassPathResource ibusConfig = new ClassPathResource("/application-default.properties");

        if (ibusConfig.exists()) {
            this.settingsFile = ibusConfig.getFile();
        } else {
            Path path = Paths.get("conf", "application-default.properties");
            if (path.toFile().exists()) {
                this.settingsFile = path.toFile();
            } else {
                path.toFile().getParentFile().mkdirs();
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
        this.elasticConfig = elasticConfig;
        this.indexManager = indexManager;
    }

    @PostConstruct
    public void init() {
        // use resolved properties values (instead of ${xxx:yyy})
        this.properties.put("codelistrepo.url", codelistConfiguration.getUrl());
        this.properties.put("codelistrepo.username", codelistConfiguration.getUsername());
        this.properties.put("codelistrepo.password", codelistConfiguration.getPassword());
        this.properties.put("elastic.remoteHosts", String.join(",", elasticConfiguration.getRemoteHosts()));
        this.properties.put("elastic.username", elasticConfiguration.getUsername());
        this.properties.put("elastic.password", elasticConfiguration.getPassword());
        this.properties.put("elastic.sslTransport", elasticConfiguration.getSslTransport());
        this.properties.put("server.port", String.valueOf( serverConfiguration.getPort() ));
        this.properties.put("ibus.url", busConfiguration.getUrl());
        this.properties.put("spring.security.user.password", springConfiguration.getUser().getPassword());

        updateElasticCheckThread();
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
        indexManager.init();
        indicesService.init();
        updateElasticCheckThread();

        // check if elasticsearch connection was established the first time and needs index "ingrid_meta"
        try {
            this.indicesService.prepareIndices();
        } catch (NoNodeAvailableException e) {
            log.warn("Elasticsearch does not seem to be configured, since we could not connect to it. Please check the settings.");
        }

        return true;
    }

    private void updateElasticCheckThread() {
        if (elasticCheck != null) {
            elasticCheck.interrupt();
        }

        Thread.UncaughtExceptionHandler exceptionHandler = (th, ex) -> log.debug("ElasticConnectionCheck Exception: " + ex);

        elasticCheck = new ElasticConnectionCheck(elasticsearchBean, elasticConfig, indexManager, indicesService);
        elasticCheck.setUncaughtExceptionHandler(exceptionHandler);
        elasticCheck.start();
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
        elasticConfiguration.setUsername((String) configuration.get("elastic.username"));
        elasticConfiguration.setPassword((String) configuration.get("elastic.password"));
        elasticConfiguration.setSslTransport((String) configuration.get("elastic.sslTransport"));
        if (remoteHosts != null) {
            try {
                String[] remoteHostsArray;
                if ("".equals(remoteHosts)) {
                    remoteHostsArray = new String[0];
                } else {
                    remoteHostsArray = remoteHosts.split(",");
                }

                elasticConfiguration.setRemoteHosts( remoteHostsArray );
                elasticConfig.remoteHosts = remoteHostsArray;
                elasticConfig.username = elasticConfiguration.getUsername();
                elasticConfig.password = elasticConfiguration.getPassword();
                elasticConfig.sslTransport = elasticConfiguration.getSslTransport();

                // when creating a new transport client, then stop elastic connection check
                // to prevent creation of multiple clients
                this.elasticCheck.interrupt();
                elasticsearchBean.createTransportClient(elasticConfig);
                updateElasticCheckThread();
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
