package de.ingrid.ibus.management;

import de.ingrid.ibus.comm.BusServer;
import de.ingrid.ibus.comm.registry.Registry;
import de.ingrid.ibus.comm.registry.RegistryConfigurable;
import de.ingrid.ibus.service.SettingsService;
import de.ingrid.ibus.service.SimulatedLifesign;
import de.ingrid.utils.PlugDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManagementService implements RegistryConfigurable {

    public static final String MANAGEMENT_IPLUG_ID = "__managementIPlug__";

    @Autowired
    private BusServer busServer;

    @Autowired
    private ManagementIPlug managementIPlug;
    
    @Autowired
    private SettingsService settings;

    private String[] fields = new String[] { "incl_meta", "login", "digest", "management_request_type" };
    private String[] datatypes = new String[] { "management" };
    private SimulatedLifesign currentSimulatedLifesign = null;

    @Override
    public void handleRegistryUpdate(Registry registry) {
        if (currentSimulatedLifesign != null) {
            currentSimulatedLifesign.close();
        }

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

        currentSimulatedLifesign = new SimulatedLifesign( registry, pd );
    }
}
