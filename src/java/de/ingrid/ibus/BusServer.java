/*
 * Copyright (c) 1997-2006 by media style GmbH
 */
package de.ingrid.ibus;

import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;

import net.weta.components.communication.ICommunication;
import net.weta.components.communication_sockets.SocketCommunication;
import net.weta.components.peer.PeerService;
import net.weta.components.peer.messaging.JxtaMessageQueue;
import net.weta.components.proxies.ProxyService;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import de.ingrid.ibus.net.IPlugProxyFactory;
import de.ingrid.ibus.net.IPlugProxyFactoryImpl;
import de.ingrid.ibus.registry.Registry;
import de.ingrid.utils.queryparser.ParseException;

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
                System.exit(1);
            }
        } else if (arguments.containsKey("--descriptor")) {
            String filename = (String) arguments.get("--descriptor");
            String busurl = (String) arguments.get("--descriptor");

            try {
                // communication = startJxtaCommunication(filename);
                communication.subscribeGroup(busurl);
            } catch (Exception e) {
                System.err.println("Cannot start the communication: " + e.getMessage());
                System.exit(1);
            }
        } else {
            System.err.println(usage);
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
        Bus bus = new Bus(proxyFactory);
        Registry registry = bus.getIPlugRegistry();
        registry.setCommunication(communication);

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // can be ignored
            }
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
