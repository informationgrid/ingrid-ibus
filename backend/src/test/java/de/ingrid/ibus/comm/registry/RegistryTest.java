/*
 * **************************************************-
 * InGrid iBus
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
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

package de.ingrid.ibus.comm.registry;

import java.util.Arrays;

import de.ingrid.ibus.service.SettingsService;
import junit.framework.TestCase;
import de.ingrid.ibus.comm.DummyCommunication;
import de.ingrid.ibus.comm.DummyProxyFactory;
import de.ingrid.utils.PlugDescription;

/**
 * 
 * created on 21.07.2005
 * <p>
 * 
 * @author hs
 */
public class RegistryTest extends TestCase {

    /**
     * 
     * @throws Exception
     */
    public void testAddAndGet() throws Exception {
        Registry registry = new Registry(1000, true, new DummyProxyFactory(), new SettingsService());
        registry.setCommunication(new DummyCommunication());
        PlugDescription plug1 = new PlugDescription();
        plug1.setProxyServiceURL("/:aID");
        registry.addPlugDescription(plug1);
        PlugDescription plug2 = registry.getPlugDescription(plug1.getPlugId());
        assertEquals(plug1, plug2);
    }

    /**
     * @throws Exception
     */
    public void testGetAllIPlugs() throws Exception {
        int plugLifeTime = 250;
        Registry registry = new Registry(plugLifeTime, true, new DummyProxyFactory(), new SettingsService());
        registry.setCommunication(new DummyCommunication());
        PlugDescription plug1 = new PlugDescription();
        plug1.setProxyServiceURL("/:aID");
        registry.addPlugDescription(plug1);

        assertTrue(Arrays.asList(registry.getAllIPlugs()).contains(plug1));
        assertTrue(Arrays.asList(registry.getAllIPlugsWithoutTimeLimitation()).contains(plug1));

        // kick plug1 out
        Thread.sleep(plugLifeTime + 1000);
        assertFalse(Arrays.asList(registry.getAllIPlugs()).contains(plug1));
        assertFalse(Arrays.asList(registry.getAllIPlugsWithoutTimeLimitation()).contains(plug1));

        // refresh plug1
        registry.addPlugDescription(plug1);
        assertTrue(Arrays.asList(registry.getAllIPlugs()).contains(plug1));
        assertEquals(1, registry.getAllIPlugsWithoutTimeLimitation().length);
    }

    /**
     * @throws Exception
     */
    public void testContains() throws Exception {
        Registry registry = new Registry(1000, true, new DummyProxyFactory(), new SettingsService());
        registry.setCommunication(new DummyCommunication());
        PlugDescription plugDescription = new PlugDescription();
        plugDescription.setProxyServiceURL("/:aID");
        assertFalse(registry.containsPlugDescription(plugDescription.getPlugId(), plugDescription.getMd5Hash()));
        registry.addPlugDescription(plugDescription);
        assertTrue(registry.containsPlugDescription(plugDescription.getPlugId(), plugDescription.getMd5Hash()));
        assertEquals(1, registry.getAllIPlugs().length);

        // chainged hash
        String oldMd5 = plugDescription.getMd5Hash();
        plugDescription = new PlugDescription();
        plugDescription.setProxyServiceURL("/:aID");
        plugDescription.setMd5Hash("newMd5");
        assertFalse(registry.containsPlugDescription(plugDescription.getPlugId(), plugDescription.getMd5Hash()));
        registry.addPlugDescription(plugDescription);
        assertTrue(registry.containsPlugDescription(plugDescription.getPlugId(), plugDescription.getMd5Hash()));
        assertFalse(registry.containsPlugDescription(plugDescription.getPlugId(), oldMd5));
        assertEquals(1, registry.getAllIPlugs().length);
        assertNotNull(registry.getPlugProxy(plugDescription.getPlugId()));
    }

}
