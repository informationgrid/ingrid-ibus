/*
 * **************************************************-
 * Ingrid iBus
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
package de.ingrid.ibus;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import net.weta.components.communication.ICommunication;
import net.weta.components.communication.reflect.ReflectMessageHandler;
import net.weta.components.communication.tcp.StartCommunication;

import org.mortbay.http.HashUserRealm;

import de.ingrid.ibus.net.IPlugProxyFactory;
import de.ingrid.ibus.net.IPlugProxyFactoryImpl;
import de.ingrid.ibus.processor.AddressPreProcessor;
import de.ingrid.ibus.processor.BusUrlPreProcessor;
import de.ingrid.ibus.processor.LimitedAttributesPreProcessor;
import de.ingrid.ibus.processor.QueryModePreProcessor;
import de.ingrid.ibus.processor.QueryModifierPreProcessor;
import de.ingrid.ibus.processor.UdkMetaclassPreProcessor;
import de.ingrid.ibus.registry.Registry;
import de.ingrid.ibus.web.AdminServer;
import de.ingrid.utils.IBus;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.metadata.IMetadataInjector;
import de.ingrid.utils.metadata.Metadata;
import de.ingrid.utils.metadata.MetadataInjectorFactory;

/**
 * The server that starts a bus and its admin web gui.
 */
public class BusServer {

    /**
     * This method is for starting a IBus-Server by hand. The four required commandline options are "--descriptor
     * <filename>", "--busurl <bus url e.g. /group:bus>" --adminport <1000-65535> and --adminpassword <password>. The
     * port and the password are for the admin web gui.
     * 
     * @param args
     *            An array of strings containing the commandline options described above.
     * @throws InterruptedException
     *             Is thrown when the bus server is interrupted by signal.
     */
    public static void main(String[] args) throws Exception {
        final String usage = "You must set --descriptor <filename>, --busurl <wetag url>, --adminport <1000-65535> and --adminpassword <password>.";
        HashMap arguments = new HashMap();
        String busurl = null;
        String adminpassword = "";
        int adminport = 0;

        // convert and validate the supplied arguments
        if (8 != args.length) {
            System.err.println("Wrong numbers of arguments. ");
            System.err.println(usage);
            return;
        }
        for (int i = 0; i < args.length; i = i + 2) {
            arguments.put(args[i], args[i + 1]);
        }

        ICommunication communication = null;
        if (arguments.containsKey("--descriptor") && arguments.containsKey("--busurl")
                && arguments.containsKey("--adminport") && arguments.containsKey("--adminpassword")) {
            String filename = (String) arguments.get("--descriptor");
            busurl = (String) arguments.get("--busurl");
            try {
                adminport = Integer.parseInt((String) arguments.get("--adminport"));
            } catch (NumberFormatException e) {
                System.err.println("--adminport isn't a number.");
                System.err.println(usage);
                return;
            }
            adminpassword = (String) arguments.get("--adminpassword");

            try {
                FileInputStream fileIS = new FileInputStream(filename);
                communication = StartCommunication.create(fileIS);
                communication.startup();
                communication.subscribeGroup(busurl);
            } catch (Exception e) {
                System.err.println("Cannot start the communication: " + e.getMessage());
                e.printStackTrace();
                return;
            }
        } else {
            System.err.println("Min. one argument is wrong named.");
            System.err.println(usage);
            return;
        }

        // instatiate the IBus
        IPlugProxyFactory proxyFactory = new IPlugProxyFactoryImpl(communication);
		Bus bus = new Bus(proxyFactory);
        Metadata metadata = new Metadata();
        injectMetadatas(metadata, bus);
		bus.setMetadata(metadata);
        Registry registry = bus.getIPlugRegistry();
        registry.setUrl(busurl);
        registry.setCommunication(communication);

        // add processors
        bus.getProccessorPipe().addPreProcessor(new UdkMetaclassPreProcessor());
        bus.getProccessorPipe().addPreProcessor(new LimitedAttributesPreProcessor());
        bus.getProccessorPipe().addPreProcessor(new QueryModePreProcessor());
        bus.getProccessorPipe().addPreProcessor(new AddressPreProcessor());
        bus.getProccessorPipe().addPreProcessor(new BusUrlPreProcessor(busurl));
        bus.getProccessorPipe().addPreProcessor(new QueryModifierPreProcessor("/querymodifier.properties"));
        

        // read in the boost for iplugs
        InputStream is = bus.getClass().getResourceAsStream("/globalRanking.properties");
       
        Properties properties = new Properties();
        try {
            properties.load(is);
        } catch (Exception e) {
            System.err.println("Problems on loading globalRanking.properties. Does it exist?");
        }
        HashMap globalRanking = new HashMap();
        for (Iterator iter = properties.keySet().iterator(); iter.hasNext();) {
            String element = (String) iter.next();
            try {
                Float value = new Float((String) properties.get(element));
                System.out.println(("add boost for iplug: ".concat(element)).concat(" with value: " + value));
                globalRanking.put(element, value);
            } catch (NumberFormatException e) {
                System.err.println("Cannot convert the value " + properties.get(element) + " from element " + element
                        + " to a Float.");
            }
        }
        registry.setGlobalRanking(globalRanking);

        // start the proxy service
        ReflectMessageHandler messageHandler = new ReflectMessageHandler();
        messageHandler.addObjectToCall(IBus.class, bus);
        communication.getMessageQueue().getProcessorRegistry().addMessageHandler(ReflectMessageHandler.MESSAGE_TYPE,
                messageHandler);

        try {
            HashUserRealm realm = new HashUserRealm("IBus");
            realm.put("admin", adminpassword);
            AdminServer.startWebContainer(new HashMap(), adminport, new File("./webapp"), true, realm);
        } catch (Exception e) {
            System.err.println("Cannot start the IBus admin server.");
            e.printStackTrace();
        }

        synchronized (BusServer.class) {
            BusServer.class.wait();
        }
    }
    
	private static void injectMetadatas(Metadata metadata, IBus bus) throws Exception {
	    // ibus has no plugdescription
        PlugDescription plugDescription = new PlugDescription();
        MetadataInjectorFactory factory = new MetadataInjectorFactory(plugDescription, bus);
        List<IMetadataInjector> metadataInjectors = factory.getMetadataInjectors();
        for (IMetadataInjector metadataInjector : metadataInjectors) {
            metadataInjector.injectMetaDatas(metadata);
        }
    }

}
