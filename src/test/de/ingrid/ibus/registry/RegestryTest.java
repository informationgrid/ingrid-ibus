/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.registry;

import junit.framework.TestCase;
import de.ingrid.iplug.PlugDescription;

/**
 * 
 * created on 21.07.2005
 * <p>
 * 
 * @author hs
 */
public class RegestryTest extends TestCase {

    /**
     * 
     * @throws Exception
     */
    public void testAddAndGet() throws Exception {
        Regestry regestry = new Regestry(1000);
        PlugDescription plug1 = new PlugDescription();
        plug1.setPlugId("a ID");
        regestry.addIPlug(plug1);
        PlugDescription plug2 = regestry.getIPlug(plug1.getPlugId());
        assertEquals(plug1, plug2);
    }

}
