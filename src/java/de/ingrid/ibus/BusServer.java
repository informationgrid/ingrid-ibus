/*
 * Copyright (c) 1997-2006 by media style GmbH
 */
package de.ingrid.ibus;

import java.io.IOException;
import java.util.HashMap;

import net.weta.components.communication_sockets.SocketCommunication;
import net.weta.components.proxies.ProxyService;
import de.ingrid.ibus.net.IPlugProxyFactory;
import de.ingrid.ibus.net.IPlugProxyFactoryImpl;

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
     */
    public static void main(String[] args) {
        int mPort = 0;
        int uPort = 0;
        HashMap arguments = new HashMap();

        // convert and validate the supplied arguments
        if (4 != args.length) {
            System.err
                    .println("Wrong numbers of arguments. You must set --multicastPort <port> and --unicastPort <port>");
            System.exit(1);
        }
        for (int i = 0; i < args.length; i = i + 2) {
            arguments.put(args[i], args[i + 1]);
        }

        if (!arguments.containsKey("--multicastPort") || !arguments.containsKey("--unicastPort")) {
            System.err.println("Wrong arguments. You must set --multicastPort <port> and --unicastPort <port>");
            System.exit(1);
        }
        try {
            mPort = (new Integer((String) arguments.get("--multicastPort"))).intValue();
            uPort = (new Integer((String) arguments.get("--unicastPort"))).intValue();
        } catch (Exception e) {
            System.err.println("The supplied ports are no numbers. Valid ports are between 1 and 65535");
            System.exit(1);
        }

        // start the communication
        SocketCommunication communication = new SocketCommunication();
        communication.setMulticastPort(mPort);
        communication.setUnicastPort(uPort);
        try {
            communication.startup();
        } catch (IOException e) {
            System.err.println("Cannot start the communication: " + e.getMessage());
            System.exit(1);
        }

        // start the proxy server
        ProxyService proxy = new ProxyService();
        proxy.setCommunication(communication);
        try {
            proxy.startup();
        } catch (IllegalArgumentException e) {
            System.err.println("Wrong arguments supplied to the proxy service: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Cannot start the proxy server: " + e.getMessage());
            System.exit(1);
        }

        // instatiate the IBus
        IPlugProxyFactory proxyFactory = new IPlugProxyFactoryImpl(communication);
        new Bus(proxyFactory);

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // can be ignored
            }
        }
    }
}
