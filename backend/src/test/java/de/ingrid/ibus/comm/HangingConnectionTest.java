/*
 * **************************************************-
 * InGrid iBus
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
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

import net.weta.components.communication.tcp.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * TODO comment for HangingConnectionTest 
 * 
 * <p/>created on 29.04.2006
 * 
 * @version $Revision: $
 * @author jz
 * @author $Author: ${lastedit}
 *  
 */
public class HangingConnectionTest {

    /**
     * @throws Exception
     */
    @Test
    public void testhangConnection() throws Exception {
        Bus bus = new Bus(new HangingPlugDummyProxyFactory(), new SettingsService());
        PlugDescription plugDescriptions = new PlugDescription();
        plugDescriptions.setProxyServiceURL("");
        plugDescriptions.setOrganisation("org");
        bus.getIPlugRegistry().addPlugDescription(plugDescriptions);
        bus.getIPlugRegistry().activatePlug("");
        long start = System.currentTimeMillis();
        try {
            bus.search(QueryStringParser.parse("hallo"), 10, 1, 100, 1000, false);
            fail();
        } catch (TimeoutException e) {
            assertTrue(start + 100 < System.currentTimeMillis());
        }
        
    }

}
