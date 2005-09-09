/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.registry;

import junit.framework.TestCase;
import de.ingrid.iplug.IIPlug;

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
        Regestry regestry = new Regestry();
        IIPlug plug1 = new DummyIPlug("id", null);
        regestry.addIPlug(plug1);
        IIPlug plug2 = regestry.getIPlug(plug1.getId());
        assertEquals(plug1, plug2);
    }

}
