package de.ingrid.ibus.service;

import de.ingrid.codelists.CodeListService;
import de.ingrid.codelists.comm.HttpCLCommunication;
import de.ingrid.codelists.model.CodeList;
import de.ingrid.elasticsearch.ElasticsearchNodeFactoryBean;
import de.ingrid.ibus.WebSecurityConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.DefaultPropertiesPersister;

import java.io.*;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class ConfigurationService {

    private static Logger log = LogManager.getLogger(ConfigurationService.class);
    private File settingsFile;

    @Value("${spring.security.user.password:}")
    private String userPassword;

    @Autowired
    private final CodeListService codeListService = null;

    @Autowired
    private final ElasticsearchNodeFactoryBean elasticsearchBean = null;

    @Autowired
    private final WebSecurityConfig webSecurityConfig = null;

    private Properties propertiesSystem;
    private Properties properties;

    private static String[] configurableProps = new String[]{
            "codelistrepo.url", "codelistrepo.username", "elastic.remoteHosts"
    };

    public ConfigurationService() throws IOException {
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

        this.properties = new Properties();
        this.properties.putAll(propertiesSystem);
        this.properties.putAll(propertiesOverride);
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
                throw e;
            }
        }

        updateBeansConfiguration(configuration);

        return true;
    }

    private void updateBeansConfiguration(Properties configuration) throws Exception {
        // update codelist repository connection
        HttpCLCommunication communication = new HttpCLCommunication();
        communication.setRequestUrl((String) configuration.get("codelistrepo.url"));
        communication.setUsername((String) configuration.get("codelistrepo.username"));
        if (configuration.get("codelistrepo.password") != null) {
            communication.setPassword((String) configuration.get("codelistrepo.password"));
        }
        codeListService.setComm(communication);

        // Elasticsearch
        String remoteHosts = (String) configuration.get("elastic.remoteHosts");
        if (remoteHosts != null && !"".equals(remoteHosts)) {
            try {
                elasticsearchBean.createTransportClient(remoteHosts.split(","));
            } catch (UnknownHostException e) {
                log.error("Error updating elasticsearch connection", e);
            }
        }

        // Admin Passwort
        String adminPassword = (String) configuration.get("spring.security.user.password");
        if (adminPassword != null && !"".equals(adminPassword)) {
            webSecurityConfig.secureWebapp(adminPassword);
            this.userPassword = adminPassword;
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

        for (String key : configurableProps) {
            sparseConfig.put(key, properties.getProperty(key));
        }

        if (userPassword.isEmpty()) {
            sparseConfig.put("needPasswordChange", "true");
        }

        return sparseConfig;
    }

    public Properties getStatus() {
        Properties props = new Properties();

        List<CodeList> codeLists = codeListService.updateFromServer(new Date().getTime());
        props.put("codelistrepo", codeLists == null ? "false" : "true");

        return props;
    }

}
