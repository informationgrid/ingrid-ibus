package de.ingrid.ibus.service;

import javax.annotation.PostConstruct;

import de.ingrid.ibus.comm.registry.RegistryConfigurable;
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
        try {
            IngridDocument response = proxy.call( targetInfo  );
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
    
    public void activate(String plugId) {
        registry.activatePlug( plugId );
    }
    
    public void deactivate(String plugId) {
        registry.deActivatePlug( plugId );
    }
}
