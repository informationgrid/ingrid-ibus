/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: $
 */

package de.ingrid.ibus;

import junit.framework.TestCase;
import net.weta.components.communication.reflect.ProxyService;
import net.weta.components.communication_sockets.SocketCommunication;
import net.weta.components.communication_sockets.util.AddressUtil;
import de.ingrid.ibus.net.IPlugProxyFactoryImpl;
import de.ingrid.utils.IPlug;
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

    private SocketCommunication[] fPlugComms = new SocketCommunication[this.fHowMuchPlugs];

    private SocketCommunication fBusComm;

    protected void setUp() throws Exception {
        this.fBusComm = new SocketCommunication();
        this.fBusComm.setMulticastPort(9191);
        this.fBusComm.setUnicastPort(9192);
        this.fBusComm.startup();

        this.fBus = new Bus(new IPlugProxyFactoryImpl(this.fBusComm));
        for (int i = 0; i < this.plugDescriptions.length; i++) {
            this.fPlugComms[i] = new SocketCommunication();
            this.fPlugComms[i].setMulticastPort(59005 + i);
            this.fPlugComms[i].setUnicastPort(60006 + i);
            this.fPlugComms[i].startup();

            ProxyService.createProxyServer(this.fPlugComms[i], IPlug.class, new DummyIPlug());
            this.plugDescriptions[i] = new PlugDescription();
            this.plugDescriptions[i].setProxyServiceURL(AddressUtil.getWetagURL("localhost", 60006 + i));
            this.plugDescriptions[i].setIPlugClass(DummyIPlug.class.getName());
            this.plugDescriptions[i].setRecordLoader(false);
            this.plugDescriptions[i].addField("ort");
            this.fBus.getIPlugRegistry().addPlugDescription(this.plugDescriptions[i]);
            this.fBus.getIPlugRegistry().activatePlug(AddressUtil.getWetagURL("localhost", 60006 + i));
        }
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        for (int i = 0; i < this.plugDescriptions.length; i++) {
            this.fPlugComms[i].shutdown();
        }
        this.fBusComm.shutdown();
    }

    // public void testConnectIBus() throws Throwable {
    // // SocketCommunication com0 = new SocketCommunication();
    // // com0.setMulticastPort(9191);
    // // com0.setUnicastPort(9192);
    // // com0.startup();
    // Bus myBus = new Bus();
    //        
    // for (int i = 0; i < 10; i++) {
    // SocketCommunication com1 = new SocketCommunication();
    // com1.setMulticastPort(9193);
    // com1.setUnicastPort(9194);
    // com1.startup();
    //
    // ProxyService proxyService = new ProxyService();
    // proxyService.setCommunication(com1);
    // proxyService.startup();
    // RemoteInvocationController controller = proxyService
    // .createRemoteInvocationController(AddressUtil.getWetagURL(
    // "localhost", 9192));
    // Bus bus = (Bus) controller.invoke(Bus.class, Bus.class.getMethod(
    // "getInstance", null), null);
    //            
    //            
    //
    // IngridQuery query = QueryStringParser.parse("fische ort:halle");
    // System.out.println(i);
    // IngridHits hits = bus.search(query, 10, 1, 10, 1000);
    // assertEquals(this.plugDescriptions.length, hits.getHits().length);
    // proxyService.shutdown();
    // com1.shutdown();
    // }
    //
    // }
    //
    /**
     * @throws Exception
     */
    public void testSearch() throws Exception {
        IngridQuery query = QueryStringParser.parse("fische ort:halle");
        for (int i = 0; i < 3; i++) {
            IngridHits hits = this.fBus.search(query, 10, 1, Integer.MAX_VALUE, 1000);
            assertEquals(this.plugDescriptions.length * 2, hits.getHits().length);
        }
    }
}
