/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.registry;

import java.util.Arrays;

import junit.framework.TestCase;
import de.ingrid.ibus.DummyCommunication;
import de.ingrid.ibus.DummyProxyFactory;
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
        Registry registry = new Registry(1000, true, new DummyProxyFactory());
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
        Registry registry = new Registry(plugLifeTime, true, new DummyProxyFactory());
        registry.setCommunication(new DummyCommunication());
        PlugDescription plug1 = new PlugDescription();
        plug1.setProxyServiceURL("/:aID");
        registry.addPlugDescription(plug1);

        assertTrue(Arrays.asList(registry.getAllIPlugs()).contains(plug1));
        assertTrue(Arrays.asList(registry.getAllIPlugsWithoutTimeLimitation()).contains(plug1));

        // kick plug1 out
        Thread.sleep(plugLifeTime + 100);
        assertFalse(Arrays.asList(registry.getAllIPlugs()).contains(plug1));
        assertTrue(Arrays.asList(registry.getAllIPlugsWithoutTimeLimitation()).contains(plug1));

        // refresh plug1
        registry.addPlugDescription(plug1);
        assertTrue(Arrays.asList(registry.getAllIPlugs()).contains(plug1));
        assertEquals(1, registry.getAllIPlugsWithoutTimeLimitation().length);
    }

    /**
     * @throws Exception
     */
    public void testContains() throws Exception {
        Registry registry = new Registry(1000, true, new DummyProxyFactory());
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
