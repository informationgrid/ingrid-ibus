/*
 * Copyright (c) 1997-2006 by media style GmbH
 */
package de.ingrid.ibus;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import net.weta.components.communication.ICommunication;
import net.weta.components.communication.reflect.ReflectMessageHandler;
import net.weta.components.peer.StartJxtaConfig;

import org.mortbay.http.HashUserRealm;

import de.ingrid.ibus.net.IPlugProxyFactory;
import de.ingrid.ibus.net.IPlugProxyFactoryImpl;
import de.ingrid.ibus.processor.AddressPreProcessor;
import de.ingrid.ibus.processor.LimitedAttributesPreProcessor;
import de.ingrid.ibus.processor.QueryModePreProcessor;
import de.ingrid.ibus.processor.UdkMetaclassPreProcessor;
import de.ingrid.ibus.registry.Registry;
import de.ingrid.iplug.AdminServer;
import de.ingrid.utils.IBus;

/**
 * 
 */
public class BusServer {

    /**
     * This method is for starting a Proxy-Server by hand. The two required commandline options are "--multicastPort
     * <port>" and "--unicastPort <port>". Every parameter need a different port where the proxy server listens on.
     * 
     * @param args
     *            An array of strings containing the commandline options described above.
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        final String usage = "You must set --descriptor <filename>, --busurl <wetag url>, --adminport <1000-65535> and --adminpassword <password>.";
        HashMap arguments = new HashMap();
        String busurl = null;
        String adminpassword = "";
        int adminport = 0;

        // convert and validate the supplied arguments
        System.out.println(args.length);
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
                communication = StartJxtaConfig.start(fileIS);
                communication.subscribeGroup(busurl);
            } catch (Exception e) {
                System.err.println("Cannot start the communication: ".concat(e.getMessage()));
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
        Registry registry = bus.getIPlugRegistry();
        registry.setUrl(busurl);
        registry.setCommunication(communication);

        // add processors
        bus.getProccessorPipe().addPreProcessor(new UdkMetaclassPreProcessor());
        bus.getProccessorPipe().addPreProcessor(new LimitedAttributesPreProcessor());
        bus.getProccessorPipe().addPreProcessor(new QueryModePreProcessor());
        bus.getProccessorPipe().addPreProcessor(new AddressPreProcessor());

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
            AdminServer.startWebContainer(adminport, new File("./webapp"), true, realm);
        } catch (Exception e) {
            System.err.println("Cannot start the IBus admin server.");
            e.printStackTrace();
        }

        synchronized (BusServer.class) {
            BusServer.class.wait();
        }
    }
}
