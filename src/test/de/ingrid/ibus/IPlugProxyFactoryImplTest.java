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
import de.ingrid.ibus.net.IPlugProxyFactoryImpl;
import de.ingrid.iplug.PlugDescription;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 *  
 */
public class IPlugProxyFactoryImplTest extends TestCase {
    
    private int fHowMuchPlugs = 3;
    
    private Bus bus;

    private PlugDescription[] plugDescriptions = new PlugDescription[this.fHowMuchPlugs];

    private SocketCommunication[] fComProxyServer = new SocketCommunication[this.fHowMuchPlugs];

    protected void setUp() throws Exception {
        SocketCommunication com = new SocketCommunication();
        com.setMulticastPort(9191);
        com.setUnicastPort(9192);
        com.startup();

        this.bus = new Bus(new IPlugProxyFactoryImpl(com));
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
            this.plugDescriptions[i].setIPlugClass(DummyIPlug.class);
            this.bus.getIPlugRegistry().addIPlug(this.plugDescriptions[i]);
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

    /**
     * @throws Exception
     */
    public void testSearch() throws Exception {
        IngridQuery query = QueryStringParser.parse("fische ort:halle");
        IngridDocument[] documents = this.bus.search(query, 10, 1, Integer.MAX_VALUE, 1000);
        assertEquals(this.plugDescriptions.length, documents.length);
    }
}
