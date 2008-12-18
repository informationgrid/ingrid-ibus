/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import junit.framework.TestCase;
import net.weta.components.communication.configuration.ClientConfiguration;
import net.weta.components.communication.configuration.ServerConfiguration;
import net.weta.components.communication.configuration.ClientConfiguration.ClientConnection;
import net.weta.components.communication.reflect.ProxyService;
import net.weta.components.communication.tcp.TcpCommunication;
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
        
        String iBusUrl = "/101tec-group:ibus";
        
        TcpCommunication com = new TcpCommunication();
        ServerConfiguration serverConfiguration = new ServerConfiguration();
		serverConfiguration.setPort(9191);
		com.configure(serverConfiguration);
        com.setPeerName(iBusUrl);
        com.startup();
        ProxyService.createProxyServer(com, IBus.class, new Bus(new DummyProxyFactory()));

        TcpCommunication com2 = new TcpCommunication();
        ClientConfiguration clientConfiguration = new ClientConfiguration();
		clientConfiguration.setName("/101tec-group:iplug");
		ClientConnection clientConnection = clientConfiguration.new ClientConnection();
		clientConnection.setServerIp("127.0.0.1");
		clientConnection.setServerPort(9191);
		clientConnection.setServerName("/101tec-group:ibus");
		clientConfiguration.addClientConnection(clientConnection);
		com2.configure(clientConfiguration);
        com2.startup();
        
        IBus bus = (IBus) ProxyService.createProxy(com2, IBus.class, iBusUrl);
        IngridHits hits = bus.search(QueryStringParser.parse("fische"), 10, 0, 10, 1000);
        assertNotNull(hits);
    }
}
