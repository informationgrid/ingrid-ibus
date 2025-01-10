/*
 * **************************************************-
 * InGrid iBus
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
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
package de.ingrid.ibus.comm;

import de.ingrid.ibus.service.SettingsService;
import de.ingrid.utils.PlugDescription;

import org.junit.jupiter.api.Test;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * TODO comment for MultiThreadSearchTest
 * 
 * <p/>created on 29.04.2006
 * 
 * @version $Revision: $
 * @author jz
 * @author $Author: ${lastedit}
 * 
 */
public class MultiThreadSearchTest {

    private static final int fSearchCount = 600;

    /**
     * @throws Exception
     */
    @Test
    public void testTreads() throws Exception {
        PlugDescription[] plugDescriptions = new PlugDescription[fSearchCount];
        Bus bus = new Bus(new DummyProxyFactory(), new SettingsService());
        for (int i = 0; i < plugDescriptions.length; i++) {
            plugDescriptions[i] = new PlugDescription();
            plugDescriptions[i].setProxyServiceURL("" + i);
            plugDescriptions[i].setOrganisation("bla");
            bus.getIPlugRegistry().addPlugDescription(plugDescriptions[i]);
        }
        IngridQuery query = QueryStringParser.parse("fische ort:halle");
        
        for (int i=0; i<fSearchCount/6; i++) {
            new TestThread(bus, query).start();
            new TestThread(bus, query).start();
            new TestThread(bus, query).start();
            new TestThread(bus, query).start();
            new TestThread(bus, query).start();
            new TestThread(bus, query).start();
        }

        System.out.println("bal");
    }

    class TestThread extends Thread {

        /**
         * @param bus
         * @param query
         */
        public TestThread(Bus bus, IngridQuery query) {
            this.bus = bus;
            this.query = query;

        }

        private Bus bus;

        private IngridQuery query;

        public void run() {
            try {
                this.bus.search(this.query, 10, 1, Integer.MAX_VALUE, 1000, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
