/*
 * **************************************************-
 * InGrid iBus
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.ingrid.ibus.comm.net.IPlugProxyFactory;
import de.ingrid.ibus.comm.net.IPlugProxyFactoryImpl;
import de.ingrid.ibus.comm.processor.AddressPreProcessor;
import de.ingrid.ibus.comm.processor.BusUrlPreProcessor;
import de.ingrid.ibus.comm.processor.LimitedAttributesPreProcessor;
import de.ingrid.ibus.comm.processor.QueryModePreProcessor;
import de.ingrid.ibus.comm.processor.QueryModifierPreProcessor;
import de.ingrid.ibus.comm.processor.UdkMetaclassPreProcessor;
import de.ingrid.ibus.comm.registry.Registry;
import de.ingrid.utils.IBus;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.metadata.IMetadataInjector;
import de.ingrid.utils.metadata.Metadata;
import de.ingrid.utils.metadata.MetadataInjectorFactory;
import net.weta.components.communication.ICommunication;
import net.weta.components.communication.reflect.ReflectMessageHandler;
import net.weta.components.communication.tcp.StartCommunication;

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
    @Value("${ibus.descriptor:conf/communication.xml}")
    private String iBusDescriptor;

    @Value("${ibus.url}")
    private String iBusUrl;

    @Value("${ibus.port:9900}")
    private String iBusPort;

    @Value("${ibus.timeout:10}")
    private String iBusTimeout;

    @Value("${ibus.maximumSize:10485760}")
    private String iBusMaximumSize;

    @Value("${ibus.threadCount:100}")
    private String iBusThreadCount;

    @Value("${ibus.handleTimeout:60}")
    private String iBusHandleTimeout;

    @Value("${ibus.queueSize:2000}")
    private String iBusQueueSize;

    /**
     * 
     * @throws Exception
     */
    public BusServer() throws Exception {}

    @PostConstruct
    public void postConstruct() throws Exception {

        ICommunication communication = null;

        try {
            writeCommunication();

            FileInputStream fileIS = new FileInputStream( iBusDescriptor );
            communication = StartCommunication.create( fileIS );

            communication.startup();
            communication.subscribeGroup( iBusUrl );
            
            // removeCommunicationFile();
        } catch (Exception e) {
            log.error( "Cannot start the communication: " + e.getMessage() );
            e.printStackTrace();
            return;
        }

        // instantiate the IBus
        IPlugProxyFactory proxyFactory = new IPlugProxyFactoryImpl( communication );
        Bus bus = new Bus( proxyFactory );
        Metadata metadata = new Metadata();
        injectMetadatas( metadata, bus );
        bus.setMetadata( metadata );
        registry = bus.getIPlugRegistry();
        registry.setUrl( iBusUrl );
        registry.setCommunication( communication );

        // add processors
        bus.getProccessorPipe().addPreProcessor( new UdkMetaclassPreProcessor() );
        bus.getProccessorPipe().addPreProcessor( new LimitedAttributesPreProcessor() );
        bus.getProccessorPipe().addPreProcessor( new QueryModePreProcessor() );
        bus.getProccessorPipe().addPreProcessor( new AddressPreProcessor() );
        bus.getProccessorPipe().addPreProcessor( new BusUrlPreProcessor( iBusUrl ) );
        bus.getProccessorPipe().addPreProcessor( new QueryModifierPreProcessor( "/querymodifier.properties" ) );

        // read in the boost for iplugs
        InputStream is = bus.getClass().getResourceAsStream( "/globalRanking.properties" );

        Properties properties = new Properties();
        try {
            properties.load( is );
        } catch (Exception e) {
            log.error( "Problems on loading globalRanking.properties. Does it exist?" );
        }
        HashMap<String, Float> globalRanking = new HashMap<String, Float>();
        for (Iterator<?> iter = properties.keySet().iterator(); iter.hasNext();) {
            String element = (String) iter.next();
            try {
                Float value = new Float( (String) properties.get( element ) );
                log.info( ("add boost for iplug: ".concat( element )).concat( " with value: " + value ) );
                globalRanking.put( element, value );
            } catch (NumberFormatException e) {
                log.error( "Cannot convert the value " + properties.get( element ) + " from element " + element
                        + " to a Float." );
            }
        }
        registry.setGlobalRanking( globalRanking );

        // start the proxy service
        ReflectMessageHandler messageHandler = new ReflectMessageHandler();
        messageHandler.addObjectToCall( IBus.class, bus );
        communication.getMessageQueue().getProcessorRegistry().addMessageHandler( ReflectMessageHandler.MESSAGE_TYPE,
                messageHandler );

    }

    private void removeCommunicationFile() {
        try {
            Files.deleteIfExists( Paths.get( iBusDescriptor ) );
        } catch (IOException e) {
            log.error( "Could not delete communication.xml", e );
        }
        
    }

    private void writeCommunication() {
        XMLStreamWriter writer = null;
        try {

            XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
            writer = outputFactory.createXMLStreamWriter( new OutputStreamWriter(new FileOutputStream( iBusDescriptor ), Charset.forName( "utf-8" ) ) );
            writer.writeStartDocument( "utf-8", "1.0" );

            writer.writeStartElement( "communication" );
            writer.writeAttribute( "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance" );
            writer.writeAttribute( "xsi:noNamespaceSchemaLocation", "communication.xsd" );

            writer.writeStartElement( "server" );
            writer.writeAttribute( "name", iBusUrl );

            writer.writeStartElement( "socket" );
            writer.writeAttribute( "port", iBusPort );
            writer.writeAttribute( "timeout", iBusTimeout );
            writer.writeEndElement();
            writer.writeStartElement( "messages" );
            writer.writeAttribute( "maximumSize", iBusMaximumSize );
            writer.writeAttribute( "threadCount", iBusThreadCount );
            writer.writeEndElement();

            writer.writeEndElement();

            writer.writeStartElement( "messages" );
            writer.writeAttribute( "handleTimeout", iBusHandleTimeout );
            writer.writeAttribute( "queueSize", iBusQueueSize );
            writer.writeEndElement();

            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();
            writer.close();

        } catch (XMLStreamException | FileNotFoundException e) {
            log.error( "Error writing communication.xml", e );
            if (writer != null) {
                try {
                    writer.close();
                } catch (XMLStreamException e1) {
                    log.error( "Writer could not be closed", e1 );
                }
            }
        }
    }

    @PreDestroy
    private void onDestroy() {
        this.registry.getCommunication().shutdown();
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
