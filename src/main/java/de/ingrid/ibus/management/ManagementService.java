package de.ingrid.ibus.management;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.ibus.comm.BusServer;
import de.ingrid.ibus.comm.registry.Registry;
import de.ingrid.ibus.service.SettingsService;
import de.ingrid.ibus.service.SimulatedLifesign;
import de.ingrid.utils.PlugDescription;

@Service
public class ManagementService {

    private static final String MANAGEMENT_IPLUG_ID = "__managementIPlug__";

    @Autowired
    private BusServer busServer;

    @Autowired
    private ManagementIPlug managementIPlug;
    
    @Autowired
    private SettingsService settings;

    private String[] fields = new String[] { "incl_meta", "login", "digest", "management_request_type" };
    private String[] datatypes = new String[] { "management" };

    @PostConstruct
    public void init() {
        
        Registry registry = busServer.getRegistry();
        PlugDescription pd = new PlugDescription();
        pd.setProxyServiceURL( MANAGEMENT_IPLUG_ID );
        pd.setIPlugClass( "managementiplug" );
        pd.setRecordLoader( true );
        pd.setRankinTypes( false, false, true );
        pd.setDataSourceDescription( "central index" );

        for (String datatype : datatypes) {
            pd.addDataType( datatype );
        }

        for (String field : fields) {
            pd.addField( field );
        }

        pd.put( "overrideProxy", managementIPlug );
        registry.addPlugDescription( pd );
        
        registry.activatePlug( MANAGEMENT_IPLUG_ID );

        new SimulatedLifesign( registry, pd );
    }
}
