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
 * $Source: $
 */

package de.ingrid.ibus.comm;

import de.ingrid.ibus.service.SettingsService;

import net.weta.components.communication.configuration.ClientConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import net.weta.components.communication.configuration.ServerConfiguration;
import net.weta.components.communication.configuration.ClientConfiguration.ClientConnection;
import net.weta.components.communication.reflect.ProxyService;
import net.weta.components.communication.tcp.TcpCommunication;
import de.ingrid.ibus.comm.net.IPlugProxyFactoryImpl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.ingrid.utils.IPlug;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * 
 */
public class IPlugProxyFactoryImplTest {

    private int fHowMuchPlugs = 3;

    private Bus fBus;

    private PlugDescription[] plugDescriptions = new PlugDescription[this.fHowMuchPlugs];

    private TcpCommunication[] fPlugComms = new TcpCommunication[this.fHowMuchPlugs];

    private TcpCommunication fBusComm;

    @BeforeEach
    public void setUp() throws Exception {
        this.fBusComm = new TcpCommunication();
        ServerConfiguration serverConfiguration = new ServerConfiguration();
		serverConfiguration.setPort(9191);
		serverConfiguration.setName("/101tec-group:ibus1");
		this.fBusComm.configure(serverConfiguration);
        this.fBusComm.setPeerName("/101tec-group:ibus1");
        this.fBusComm.startup();

        this.fBus = new Bus(new IPlugProxyFactoryImpl(this.fBusComm), new SettingsService());
        for (int i = 0; i < this.plugDescriptions.length; i++) {
            this.fPlugComms[i] = new TcpCommunication();
            ClientConfiguration clientConfiguration = new ClientConfiguration();
			clientConfiguration.setName("/101tec-group:iplug" + i);
			ClientConnection clientConnection = clientConfiguration.new ClientConnection();
			clientConnection.setServerIp("127.0.0.1");
			clientConnection.setServerPort(9191);
			clientConnection.setServerName("/101tec-group:ibus1");
			clientConfiguration.addClientConnection(clientConnection);
			this.fPlugComms[i].configure(clientConfiguration);
            this.fPlugComms[i].setPeerName("/101tec-group:iplug" + i);
            this.fPlugComms[i].startup();

            DummyIPlug dummyIPlug = new DummyIPlug();
            dummyIPlug.setDocId( i+1 );
            ProxyService.createProxyServer(this.fPlugComms[i], IPlug.class, dummyIPlug);
            this.plugDescriptions[i] = new PlugDescription();
            this.plugDescriptions[i].setProxyServiceURL("/101tec-group:iplug" + i);
            this.plugDescriptions[i].setIPlugClass(DummyIPlug.class.getName());
            this.plugDescriptions[i].setRecordLoader(false);
            this.plugDescriptions[i].addField("ort");
            this.fBus.getIPlugRegistry().addPlugDescription(this.plugDescriptions[i]);
            this.fBus.getIPlugRegistry().activatePlug("/101tec-group:iplug"+i);
        }
    }

    /**
     * @see 
     */
    @AfterEach
    public void tearDown() throws Exception {
        for (int i = 0; i < this.plugDescriptions.length; i++) {
            this.fPlugComms[i].shutdown();
        }
        this.fBusComm.shutdown();
        this.fBus.close();
    }

    /**
     * @throws Exception
     */
    @Test
    public void testSearch() throws Exception {
        IngridQuery query = QueryStringParser.parse("fische ort:halle");
        for (int i = 0; i < 3; i++) {
            IngridHits hits = this.fBus.search(query, 10, 1, Integer.MAX_VALUE, 1000, false);
            assertEquals(this.plugDescriptions.length * 2, hits.getHits().length);
        }
    }
}
