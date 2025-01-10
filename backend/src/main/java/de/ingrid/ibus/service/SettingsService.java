/*-
 * **************************************************-
 * ingrid-ibus-backend
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.StringUtils;

@Service
public class SettingsService {

    private static Logger log = LogManager.getLogger( SettingsService.class );
    private File settingsFile;

    private Set<String> activeIndices = null;
    private Properties properties;

    public SettingsService() {
        ClassPathResource ibusSettings = new ClassPathResource( "/activatedIplugs.properties" );

        try {
            if (ibusSettings.exists()) {
                this.settingsFile = ibusSettings.getFile();
            } else {
                Path path = Paths.get( "conf", "activatedIplugs.properties" );
                if (path.toFile().exists()) {
                    this.settingsFile = path.toFile();
                } else {
                    // if conf dir does not exist then create it
                    if (!path.getParent().toFile().exists()) {
                        Files.createDirectories(path.getParent());
                    }
                    this.settingsFile = Files.createFile( path ).toFile();
                }
            }

            // create a sorted properties file
            properties = new Properties() {
                private static final long serialVersionUID = 6956076060462348684L;
                @Override
                public synchronized Enumeration<Object> keys() {
                    return Collections.enumeration(new TreeSet<>(super.keySet()));
                }
            };
            
            properties.load( new FileReader( this.settingsFile ) );

            String propActiveIndices = properties.getProperty( "activeIndices" );
            if (propActiveIndices != null) {
                
                List<String> activeList = propActiveIndices.trim().length() == 0 
                        ? new ArrayList<>()
                        : Arrays.asList( propActiveIndices.split( "," ) );
                        
                activeIndices = new HashSet<>( activeList );
            } else {
                activeIndices = new HashSet<>();
            }

        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error( "Cannot open the file for saving the activation state of the iplugs.", e );
            }
        }
    }
    
    public Set<String> getActiveComponentIds() {
        return this.activeIndices;
    }

    public boolean activateIndexType(String index) throws Exception {
        activeIndices.add( index );
        properties.put( "activeIndices", StringUtils.collectionToCommaDelimitedString( activeIndices ) );
        return writeSettings();
    }
    
    public boolean deactivateIndexType(String index) throws Exception {
        activeIndices.remove( index );
        properties.put( "activeIndices", StringUtils.collectionToCommaDelimitedString( activeIndices ) );
        return writeSettings();
    }
    
    private boolean writeSettings() throws Exception {
        DefaultPropertiesPersister p = new DefaultPropertiesPersister();
        OutputStream out = null;
        try {
            out = new FileOutputStream( settingsFile );
            p.store( properties, out, "Written by SettingsService" );
        } catch (Exception e) {
            log.error("Error writing settings", e);
            throw e;
        } finally {
            try {
                if (out != null) out.close();
            } catch (IOException e) {
                log.error("Error writing active indices", e);
                throw e;
            }
        }
        return true;
    }

    public boolean isActive(String indexName) {
        return activeIndices.contains( indexName );
    }

    public boolean activateIPlug(String id) {
        properties.put( id, "true" );
        try {
            return writeSettings();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deactivateIPlug(String id) {
        properties.put( id, "false" );
        try {
            return writeSettings();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean containsIPlug(String proxyServiceURL) {
        return this.properties.containsKey(proxyServiceURL);
    }

    public boolean isIPlugActivated(String proxyServiceURL) {
        return "true".equals(this.properties.get(proxyServiceURL));
    }
}
