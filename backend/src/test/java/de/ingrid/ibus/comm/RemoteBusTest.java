/*
 * **************************************************-
 * InGrid iBus
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.comm;

import de.ingrid.ibus.service.SettingsService;

import net.weta.components.communication.configuration.ClientConfiguration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import net.weta.components.communication.configuration.ServerConfiguration;
import net.weta.components.communication.configuration.ClientConfiguration.ClientConnection;
import net.weta.components.communication.reflect.ProxyService;
import net.weta.components.communication.tcp.TcpCommunication;
import de.ingrid.utils.IBus;

import org.junit.jupiter.api.Test;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * 
 */
public class RemoteBusTest {

    /**
     * @throws Throwable
     */
    @Test
    public void testASearch() throws Throwable {
        String iBusUrl = "/101tec-group:ibus";
        
        TcpCommunication com = new TcpCommunication();
        ServerConfiguration serverConfiguration = new ServerConfiguration();
		serverConfiguration.setPort(9191);
		com.configure(serverConfiguration);
        com.setPeerName(iBusUrl);
        com.startup();
        ProxyService.createProxyServer(com, IBus.class, new Bus(new DummyProxyFactory(), new SettingsService()));

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


    @Test
    public void testRestartServer() throws Throwable {
        
        String iBusUrl = "/101tec-group:ibus";
        
        TcpCommunication com = new TcpCommunication();
        ServerConfiguration serverConfiguration = new ServerConfiguration();
		serverConfiguration.setPort(9192);
		com.configure(serverConfiguration);
        com.setPeerName(iBusUrl);
        com.startup();
        ProxyService.createProxyServer(com, IBus.class, new Bus(new DummyProxyFactory(), new SettingsService()));

        TcpCommunication com2 = new TcpCommunication();
        ClientConfiguration clientConfiguration = new ClientConfiguration();
		clientConfiguration.setName("/101tec-group:iplug");
		ClientConnection clientConnection = clientConfiguration.new ClientConnection();
		clientConnection.setServerIp("127.0.0.1");
		clientConnection.setServerPort(9192);
		clientConnection.setServerName("/101tec-group:ibus");
		clientConfiguration.addClientConnection(clientConnection);
		com2.configure(clientConfiguration);
        com2.startup();
        
        Thread.sleep(3000);
        
        IBus bus = (IBus) ProxyService.createProxy(com2, IBus.class, iBusUrl);

        Thread.sleep(1000);
        
        IngridHits hits = bus.search(QueryStringParser.parse("fische"), 10, 0, 10, 1000);
        assertNotNull(hits);
        
        System.out.println("Shuting down iBus...");
        com.shutdown();
        System.out.println("Shuting down iBus...done.");
        
        Thread.sleep(3000);
        
        System.out.println("Restart iBus...");
        com = new TcpCommunication();
		com.configure(serverConfiguration);
        com.setPeerName(iBusUrl);
        com.startup();
        ProxyService.createProxyServer(com, IBus.class, new Bus(new DummyProxyFactory(), new SettingsService()));
        System.out.println("Restart iBus... done.");
        
        Thread.sleep(1000);

        hits = bus.search(QueryStringParser.parse("fische"), 10, 0, 10, 1000);
        assertNotNull(hits);
    }


    @Test
    public void testRestartClient() throws Throwable {
        
        String iBusUrl = "/101tec-group:ibus";
        
        TcpCommunication com = new TcpCommunication();
        ServerConfiguration serverConfiguration = new ServerConfiguration();
		serverConfiguration.setPort(9193);
		com.configure(serverConfiguration);
        com.setPeerName(iBusUrl);
        com.startup();
        
        Thread.sleep(3000);
        
        ProxyService.createProxyServer(com, IBus.class, new Bus(new DummyProxyFactory(), new SettingsService()));
        TcpCommunication com2 = new TcpCommunication();
        ClientConfiguration clientConfiguration = new ClientConfiguration();
		clientConfiguration.setName("/101tec-group:iplug");
		ClientConnection clientConnection = clientConfiguration.new ClientConnection();
		clientConnection.setServerIp("127.0.0.1");
		clientConnection.setServerPort(9193);
		clientConnection.setServerName("/101tec-group:ibus");
		clientConfiguration.addClientConnection(clientConnection);
		com2.configure(clientConfiguration);
        com2.startup();
        
        IBus bus = (IBus) ProxyService.createProxy(com2, IBus.class, iBusUrl);

        Thread.sleep(1000);
        
        IngridHits hits = bus.search(QueryStringParser.parse("fische"), 10, 0, 10, 1000);
        assertNotNull(hits);
        
        System.out.println("Shuting down client...");
        bus.close();
        com2.shutdown();
        System.out.println("Shuting down client...done.");
        
        Thread.sleep(3000);
        
        System.out.println("Restart client...");
        com2 = new TcpCommunication();
		com2.configure(clientConfiguration);
        com2.startup();
        bus = (IBus) ProxyService.createProxy(com2, IBus.class, iBusUrl);
        System.out.println("Restart client... done.");

        Thread.sleep(1000);
        
        hits = bus.search(QueryStringParser.parse("fische"), 10, 0, 10, 1000);
        assertNotNull(hits);

        
    }    

}
