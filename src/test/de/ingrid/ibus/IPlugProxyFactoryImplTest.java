/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: $
 */

package de.ingrid.ibus;

import junit.framework.TestCase;
import net.weta.components.communication_sockets.SocketCommunication;
import net.weta.components.communication_sockets.util.AddressUtil;
import net.weta.components.proxies.ProxyService;
import net.weta.components.proxies.remote.RemoteInvocationController;
import de.ingrid.ibus.net.IPlugProxyFactoryImpl;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 *  
 */
public class IPlugProxyFactoryImplTest extends TestCase {
    
    private int fHowMuchPlugs = 3;
    
    private Bus fBus;

    private PlugDescription[] plugDescriptions = new PlugDescription[this.fHowMuchPlugs];

    private SocketCommunication[] fComProxyServer = new SocketCommunication[this.fHowMuchPlugs];

    protected void setUp() throws Exception {
        SocketCommunication com = new SocketCommunication();
        com.setMulticastPort(9191);
        com.setUnicastPort(9192);
        com.startup();

        this.fBus = new Bus(new IPlugProxyFactoryImpl(com));
        for (int i = 0; i < this.plugDescriptions.length; i++) {
            this.fComProxyServer[i] = new SocketCommunication();
            this.fComProxyServer[i].setMulticastPort(50005 + i);
            this.fComProxyServer[i].setUnicastPort(60006 + i);
            this.fComProxyServer[i].startup();
            
            String comProxyServerUrl = AddressUtil.getWetagURL("localhost", 60006 + i);
            
            ProxyService proxyService = new ProxyService();
            proxyService.setCommunication(this.fComProxyServer[i]);
            proxyService.startup();

            this.plugDescriptions[i] = new PlugDescription();
            this.plugDescriptions[i].setPlugId("" + i);
            this.plugDescriptions[i].setProxyServiceURL(comProxyServerUrl);
            this.plugDescriptions[i].setIPlugClass(DummyIPlug.class.getName());
            this.fBus.getIPlugRegistry().addIPlug(this.plugDescriptions[i]);
        }
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        for (int i = 0; i < this.plugDescriptions.length; i++) {
            this.fComProxyServer[i].shutdown();
        }
        super.tearDown();
    }
    
//    public void testConnectIBus() throws Throwable {
////        SocketCommunication com0 = new SocketCommunication();
////        com0.setMulticastPort(9191);
////        com0.setUnicastPort(9192);
////        com0.startup();
//        Bus myBus = new Bus();
//        
//        for (int i = 0; i < 10; i++) {
//            SocketCommunication com1 = new SocketCommunication();
//            com1.setMulticastPort(9193);
//            com1.setUnicastPort(9194);
//            com1.startup();
//
//            ProxyService proxyService = new ProxyService();
//            proxyService.setCommunication(com1);
//            proxyService.startup();
//            RemoteInvocationController controller = proxyService
//                    .createRemoteInvocationController(AddressUtil.getWetagURL(
//                            "localhost", 9192));
//            Bus bus = (Bus) controller.invoke(Bus.class, Bus.class.getMethod(
//                    "getInstance", null), null);
//            
//            
//
//            IngridQuery query = QueryStringParser.parse("fische ort:halle");
//            System.out.println(i);
//            IngridHits hits = bus.search(query, 10, 1, 10, 1000);
//            assertEquals(this.plugDescriptions.length, hits.getHits().length);
//            proxyService.shutdown();
//            com1.shutdown();
//        }
//
//    }
//
    /**
     * @throws Exception
     */
    public void testSearch() throws Exception {
        IngridQuery query = QueryStringParser.parse("fische ort:halle");
        for (int i = 0; i < 3; i++) {
           System.out.println(i);
            IngridHits hits = this.fBus.search(query, 10, 1, Integer.MAX_VALUE,
                    1000);
            assertEquals(this.plugDescriptions.length, hits.getHits().length);
        }
    }
}
