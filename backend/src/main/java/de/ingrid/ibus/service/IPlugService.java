/*-
 * **************************************************-
 * ingrid-ibus-backend
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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

import javax.annotation.PostConstruct;

import de.ingrid.ibus.comm.registry.RegistryConfigurable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.ibus.comm.BusServer;
import de.ingrid.ibus.comm.registry.Registry;
import de.ingrid.utils.IPlug;
import de.ingrid.utils.IngridCall;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.PlugDescription;

@Service
public class IPlugService implements RegistryConfigurable {

    private static Logger log = LogManager.getLogger(IPlugService.class);

    private Registry registry;
    
    @Override
    public void handleRegistryUpdate(Registry registry) {
        this.registry = registry;
    }
    
    public PlugDescription[] getConnectedIPlugs() {
        return registry.getAllIPlugs();
    }
    
    public boolean index(String plugId) {
        IPlug proxy = registry.getPlugProxy( plugId );
        
        if (proxy == null) {
            return false;
        }
        
        IngridCall targetInfo = new IngridCall();
        targetInfo.setMethod( "index" );
        targetInfo.setParameter("doNotUseCache: " + Math.random());
        try {
            IngridDocument response = proxy.call( targetInfo  );
            return true;
        } catch (Exception e) {
            log.error("Error calling index method for an iPlug", e);
            return false;
        }
    }

    public PlugDescription getIPlugDetail(String id) {
        for (PlugDescription pd : getConnectedIPlugs()) {
            if (pd.getPlugId().equals( id )) {
                return pd;
            }
        }
        return null;
    }

    /**
     * Check if an iPlug is connected to the iBus via the communication.
     * @param plugId is the ID of the iPlug
     * @return true if iPlug is connected otherwise false
     */
    public boolean isConnectedDirectly(String plugId) {
        PlugDescription[] allIPlugsConnected = registry.getAllIPlugsConnected();
        for (PlugDescription iPlug : allIPlugsConnected) {
            if (iPlug.getProxyServiceURL().equals(plugId)) {
                return true;
            }
        }
        return false;
    }
    
    public void activate(String plugId) {
        registry.activatePlug( plugId );
    }
    
    public void deactivate(String plugId) {
        registry.deActivatePlug( plugId );
    }
}
