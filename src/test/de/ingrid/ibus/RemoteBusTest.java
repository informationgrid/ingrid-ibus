/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import junit.framework.TestCase;
import net.weta.components.communication.reflect.ProxyService;
import net.weta.components.communication_sockets.SocketCommunication;
import net.weta.components.communication_sockets.util.AddressUtil;
import de.ingrid.utils.IBus;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * 
 */
public class RemoteBusTest extends TestCase {

    /**
     * @throws Throwable
     */
    public void testSearch() throws Throwable {
        SocketCommunication com = new SocketCommunication();
        com.setMulticastPort(10022);
        com.setUnicastPort(10023);
        com.startup();
        ProxyService.createProxyServer(com, IBus.class, new Bus(new DummyProxyFactory()));

        String iBusUrl = AddressUtil.getWetagURL("localhost", 10023);
        IBus bus = (IBus) ProxyService.createProxy(com, IBus.class, iBusUrl);
        IngridHits hits = bus.search(QueryStringParser.parse("fische"), 10, 0, 10, 1000);
        assertNotNull(hits);
    }
}
