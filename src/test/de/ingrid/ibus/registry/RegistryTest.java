/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.registry;

import java.util.Arrays;

import junit.framework.TestCase;
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
        Registry registry = new Registry(1000);
        PlugDescription plug1 = new PlugDescription();
        plug1.setPlugId("a ID");
        registry.addIPlug(plug1);
        PlugDescription plug2 = registry.getPlugDescription(plug1.getPlugId());
        assertEquals(plug1, plug2);
    }

    /**
     * @throws Exception
     */
    public void testGetAllIPlugs() throws Exception {
        int plugLifeTime=250;
        Registry registry = new Registry(plugLifeTime);
        PlugDescription plug1 = new PlugDescription();
        plug1.setPlugId("a ID");
        registry.addIPlug(plug1);
        
        assertTrue(Arrays.asList(registry.getAllIPlugs()).contains(plug1));
        assertTrue(Arrays.asList(registry.getAllIPlugsWithoutTimeLimitation()).contains(plug1));
        
        //kick plug1 out
        Thread.sleep(plugLifeTime+100);
        assertFalse(Arrays.asList(registry.getAllIPlugs()).contains(plug1));
        assertTrue(Arrays.asList(registry.getAllIPlugsWithoutTimeLimitation()).contains(plug1));
        
        //refresh plug1 
        registry.addIPlug(plug1);
        assertTrue(Arrays.asList(registry.getAllIPlugs()).contains(plug1));
        assertEquals(1,registry.getAllIPlugsWithoutTimeLimitation().length);
    }

}
