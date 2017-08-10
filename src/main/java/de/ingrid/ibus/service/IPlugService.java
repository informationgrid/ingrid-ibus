package de.ingrid.ibus.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.ibus.comm.BusServer;
import de.ingrid.ibus.comm.registry.Registry;
import de.ingrid.utils.IPlug;
import de.ingrid.utils.IngridCall;
import de.ingrid.utils.IngridDocument;

@Service
public class IPlugService {

    @Autowired 
    private BusServer busServer;
    
    private Registry registry;
    
    @PostConstruct
    public void init() {
        registry = busServer.getRegistry();
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
}
