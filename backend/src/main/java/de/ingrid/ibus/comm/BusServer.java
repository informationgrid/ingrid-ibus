/*
 * **************************************************-
 * InGrid iBus
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
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
/*
 * Copyright (c) 1997-2006 by media style GmbH
 */
package de.ingrid.ibus.comm;

import de.ingrid.ibus.comm.net.IPlugProxyFactory;
import de.ingrid.ibus.comm.net.IPlugProxyFactoryImpl;
import de.ingrid.ibus.comm.processor.*;
import de.ingrid.ibus.comm.registry.Registry;
import de.ingrid.ibus.comm.registry.RegistryConfigurable;
import de.ingrid.ibus.config.IBusConfiguration;
import de.ingrid.ibus.service.SettingsService;
import de.ingrid.utils.IBus;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.metadata.IMetadataInjector;
import de.ingrid.utils.metadata.Metadata;
import de.ingrid.utils.metadata.MetadataInjectorFactory;
import de.ingrid.utils.processor.IPreProcessor;
import net.weta.components.communication.ICommunication;
import net.weta.components.communication.configuration.ServerConfiguration;
import net.weta.components.communication.reflect.ReflectMessageHandler;
import net.weta.components.communication.tcp.StartCommunication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * The server that starts a bus and its admin web gui.
 */
@Component
public class BusServer {

    private static Logger log = LogManager.getLogger( BusServer.class );

    private Registry registry;

    /**
     * IBUS SETTINGS
     */
    @Value("${ibus.timeout:10}")
    private int iBusTimeout;

    @Value("${ibus.maximumSize:10485760}")
    private int iBusMaximumSize;

    @Value("${ibus.threadCount:100}")
    private int iBusThreadCount;

    @Value("${ibus.handleTimeout:60}")
    private int iBusHandleTimeout;

    @Value("${ibus.queueSize:2000}")
    private int iBusQueueSize;

    @Autowired
    private IBusConfiguration busConfiguration;

    @Autowired
    private RegistryConfigurable[] classesUsingRegistry = null;

    @Autowired
    private SettingsService settingsService;

    /**
     * 
     *
     */
    public BusServer() {}

    @PostConstruct
    public void postConstruct() throws Exception {
        configureIBus();
    }

    @PreDestroy
    private void onDestroy() {
        this.registry.getCommunication().shutdown();
    }

    public void configureIBus() throws Exception {
        ICommunication communication;

        if (Bus.getInstance() != null) {
            Registry iPlugRegistry = Bus.getInstance().getIPlugRegistry();
            ICommunication previousCommunication = iPlugRegistry.getCommunication();
            if (previousCommunication != null) previousCommunication.shutdown();

            // List<String> registeredClients = ((TcpCommunication) iPlugRegistry.getCommunication()).getRegisteredClients();

            for (PlugDescription iPlug : iPlugRegistry.getAllIPlugs()) {
                iPlugRegistry.getCommunication().closeConnection(iPlug.getPlugId());
                iPlugRegistry.removePlug(iPlug.getPlugId());
            }
        }

        try {
            ServerConfiguration serverConfiguration = new ServerConfiguration();
            serverConfiguration.setName(busConfiguration.getUrl());
            serverConfiguration.setPort(busConfiguration.getPort());
            serverConfiguration.setSocketTimeout(iBusTimeout);
            serverConfiguration.setHandleTimeout(iBusHandleTimeout);
            serverConfiguration.setMaxMessageSize(iBusMaximumSize);
            serverConfiguration.setMessageThreadCount(iBusThreadCount);
            serverConfiguration.setQueueSize(iBusQueueSize);

            communication = StartCommunication.create( serverConfiguration );

            communication.startup();
            communication.subscribeGroup( busConfiguration.getUrl() );

            // removeCommunicationFile();
        } catch (Exception e) {
            log.error( "Cannot start the communication: ", e );
            return;
        }

        // instantiate the IBus
        IPlugProxyFactory proxyFactory = new IPlugProxyFactoryImpl( communication );
        Bus bus = new Bus( proxyFactory, settingsService );
        Metadata metadata = new Metadata();
        injectMetadatas( metadata, bus );
        bus.setMetadata( metadata );
        registry = bus.getIPlugRegistry();
        registry.setUrl( busConfiguration.getUrl() );
        registry.setCommunication( communication );

        // remove all processors first
        IPreProcessor[] preProcessors = bus.getProccessorPipe().getPreProcessors();
        for (IPreProcessor pre: preProcessors) {
            bus.getProccessorPipe().removePreProcessor(pre);
        }

        // add processors
        bus.getProccessorPipe().addPreProcessor( new UdkMetaclassPreProcessor() );
        bus.getProccessorPipe().addPreProcessor( new LimitedAttributesPreProcessor() );
        bus.getProccessorPipe().addPreProcessor( new QueryModePreProcessor() );
        bus.getProccessorPipe().addPreProcessor( new AddressPreProcessor() );
        bus.getProccessorPipe().addPreProcessor( new BusUrlPreProcessor( busConfiguration.getUrl() ) );
        bus.getProccessorPipe().addPreProcessor( new QueryModifierPreProcessor( "/querymodifier.properties" ) );

        // read in the boost for iplugs
        InputStream is = bus.getClass().getResourceAsStream( "/globalRanking.properties" );

        Properties properties = new Properties();
        try {
            properties.load( is );
        } catch (Exception e) {
            log.error( "Problems on loading globalRanking.properties. Does it exist?" );
        }
        HashMap<String, Float> globalRanking = new HashMap<>();
        for (Object prop : properties.keySet()) {
            String element = (String) prop;
            try {
                Float value = new Float((String) properties.get(element));
                log.info(("add boost for iplug: ".concat(element)).concat(" with value: " + value));
                globalRanking.put(element, value);
            } catch (NumberFormatException e) {
                log.error("Cannot convert the value " + properties.get(element) + " from element " + element
                        + " to a Float.");
            }
        }
        registry.setGlobalRanking( globalRanking );

        // start the proxy service
        ReflectMessageHandler messageHandler = new ReflectMessageHandler();
        messageHandler.addObjectToCall( IBus.class, bus );
        registry.getCommunication().getMessageQueue().getProcessorRegistry().addMessageHandler( ReflectMessageHandler.MESSAGE_TYPE,
                messageHandler );

        // update all classes that use the registry, since it has changed now (new Bus())
        for (RegistryConfigurable registryConfigurable : classesUsingRegistry) {
            registryConfigurable.handleRegistryUpdate(registry);
        }
    }

    private void injectMetadatas(Metadata metadata, IBus bus) throws Exception {
        // ibus has no plugdescription
        PlugDescription plugDescription = new PlugDescription();
        MetadataInjectorFactory factory = new MetadataInjectorFactory( plugDescription, bus );
        List<IMetadataInjector> metadataInjectors = factory.getMetadataInjectors();
        for (IMetadataInjector metadataInjector : metadataInjectors) {
            metadataInjector.injectMetaDatas( metadata );
        }
    }

    public Registry getRegistry() {
        return registry;
    }

}
