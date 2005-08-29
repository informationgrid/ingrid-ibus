/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.registry;

import de.ingrid.iplug.IIPlug;
import de.ingrid.utils.IngridQuey;
import junit.framework.TestCase;

public class RegestryTest extends TestCase {

    public void testAddAndGet() throws Exception {
        Regestry regestry = new Regestry();
        IIPlug plug1 = new DummyIPlug(null, null);
        regestry.addIPlug(plug1);
        IIPlug plug2 = regestry.getIPlug(plug1.geId());
        assertEquals(plug1, plug2);
    }

    public void testGetIplugsForQuery() throws Exception {
        Regestry regestry = new Regestry();
        for (int i = 0; i < 10; i++) {
            String dummy = "" + i;
            regestry.addIPlug(new DummyIPlug(dummy, new String[] { dummy }));
        }
        for (int i = 0; i < 10; i++) {
            String dummy = "" + i;
            IngridQuey quey = new IngridQuey(i, dummy);
            IIPlug[] plugsForQuery = regestry.getIPlugsForQuery(quey);
            assertEquals(1, plugsForQuery.length);
        }

    }

}
