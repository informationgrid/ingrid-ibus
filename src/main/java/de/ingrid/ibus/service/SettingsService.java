package de.ingrid.ibus.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

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
                    this.settingsFile = Files.createFile( path ).toFile();
                }
            }

            properties = new Properties();
            properties.load( ibusSettings.getInputStream() );

            String propActiveIndices = properties.getProperty( "activeIndices" );
            if (propActiveIndices != null) {
                
                List<String> activeList = propActiveIndices.trim().length() == 0 
                        ? new ArrayList<String>()
                        : Arrays.asList( propActiveIndices.split( "," ) );
                        
                activeIndices = new HashSet<String>( activeList );
            } else {
                activeIndices = new HashSet<String>();
            }

        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error( "Cannot open the file for saving the activation state of the iplugs.", e );
            }
        }
    }
    
    public Set<String> getActiveIndices() {
        return this.activeIndices;
    }

    public boolean activateIndex(String index) throws Exception {
        activeIndices.add( index );
        properties.put( "activeIndices", StringUtils.collectionToCommaDelimitedString( activeIndices ) );
        return writeSettings();
    }
    
    public boolean deactivateIndex(String index) throws Exception {
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
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
            // return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
            // return false;
        } finally {
            try {
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
                // return false;
            }
        }
        return true;
    }

    public boolean isActive(String indexName) {
        return activeIndices.contains( indexName );
    }
}
