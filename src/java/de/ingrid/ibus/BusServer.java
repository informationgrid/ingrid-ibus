/*
 * Copyright (c) 1997-2006 by media style GmbH
 */
package de.ingrid.ibus;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import net.weta.components.communication.ICommunication;
import net.weta.components.communication.reflect.ReflectMessageHandler;
import net.weta.components.communication_sockets.SocketCommunication;
import net.weta.components.peer.StartJxtaConfig;
import de.ingrid.ibus.net.IPlugProxyFactory;
import de.ingrid.ibus.net.IPlugProxyFactoryImpl;
import de.ingrid.ibus.registry.Registry;
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
        final String usage = "Wrong numbers of arguments. You must set --descriptor <filename> and --busurl "
                + "<wetag url> for jxta or  --multicastPort <port> and --unicastPort <port> for socket communication.";
        HashMap arguments = new HashMap();

        // convert and validate the supplied arguments
        if (4 != args.length) {
            System.err.println(usage);
            System.exit(1);
        }
        for (int i = 0; i < args.length; i = i + 2) {
            arguments.put(args[i], args[i + 1]);
        }

        ICommunication communication = null;
        if (arguments.containsKey("--multicastPort") && arguments.containsKey("--unicastPort")) {
            int mPort = 0;
            int uPort = 0;

            try {
                mPort = (new Integer((String) arguments.get("--multicastPort"))).intValue();
                uPort = (new Integer((String) arguments.get("--unicastPort"))).intValue();
            } catch (Exception e) {
                System.err.println("The supplied ports are no numbers. Valid ports are between 1 and 65535");
                System.exit(1);
            }

            try {
                communication = startSocketCommunication(uPort, mPort);
            } catch (Exception e) {
                System.err.println("Cannot start the communication: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        } else if (arguments.containsKey("--descriptor")) {
            String filename = (String) arguments.get("--descriptor");
            String busurl = (String) arguments.get("--busurl");

            try {
                FileInputStream fileIS = new FileInputStream(filename);
                communication = StartJxtaConfig.start(fileIS);
                communication.subscribeGroup(busurl);
            } catch (Exception e) {
                System.err.println("Cannot start the communication: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            System.err.println(usage);
            System.exit(1);
        }

        // instatiate the IBus
        IPlugProxyFactory proxyFactory = new IPlugProxyFactoryImpl(communication);
        Bus bus = new Bus(proxyFactory);
        Registry registry = bus.getIPlugRegistry();
        registry.setCommunication(communication);

        // read in the boost for iplugs
        InputStream is = bus.getClass().getResourceAsStream("/globalRanking.properties");
        Properties properties = new Properties();
        try {
            properties.load(is);
        } catch (Exception e) {
            System.err.println("Problems on loading globalRanking.properties");
            e.printStackTrace();
        }
        HashMap globalRanking = new HashMap();
        for (Iterator iter = properties.keySet().iterator(); iter.hasNext();) {
            String element = (String) iter.next();
            try {
                Float value = new Float((String) properties.get(element));
                System.out.println("add boost for iplug: " + element + " with value: " + value);
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

        synchronized (BusServer.class) {
            BusServer.class.wait();
        }
    }

    private static ICommunication startSocketCommunication(int uPort, int mPort) throws Exception {
        SocketCommunication communication = new SocketCommunication();
        communication.setMulticastPort(mPort);
        communication.setUnicastPort(uPort);
        communication.startup();

        return communication;
    }
}
