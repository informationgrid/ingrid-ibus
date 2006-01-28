/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import junit.framework.TestCase;
import net.weta.components.communication_sockets.SocketCommunication;
import net.weta.components.communication_sockets.util.AddressUtil;
import net.weta.components.proxies.ProxyService;
import net.weta.components.proxies.remote.RemoteInvocationController;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.queryparser.QueryStringParser;

public class RemoteBusTest extends TestCase {

    public void testSearch() throws Throwable {
        try {

            SocketCommunication com = new SocketCommunication();
            com.setMulticastPort(10022);
            com.setUnicastPort(10023);
            com.startup();

            ProxyService proxyService = new ProxyService();
            proxyService.setCommunication(com);
            proxyService.startup();

            String iBusUrl = AddressUtil.getWetagURL("localhost", 10023);
            RemoteInvocationController ric = proxyService
                    .createRemoteInvocationController(iBusUrl);
            // to be sure there is an instance
            new Bus(new DummyProxyFactory());
            ric.newInstance(Bus.class, null, null);
            Bus bus = (Bus) ric.invoke(Bus.class, Bus.class.getMethod(
                    "getInstance", null), null);
            IngridHits hits = bus.search(QueryStringParser.parse("fische"), 10,
                    0, 10, 1000);
            assertNotNull(hits);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("TODO MASCHA please FIX THIS");
        }

    }

}
