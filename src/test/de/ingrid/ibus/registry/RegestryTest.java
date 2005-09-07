/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.registry;

import java.io.StringReader;

import junit.framework.TestCase;
import de.ingrid.iplug.IIPlug;
import de.ingrid.utils.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

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

    /**
     * 
     * @throws Exception
     */
    public void testGetIplugsForQuery() throws Exception {
        Regestry regestry = new Regestry();
        for (int i = 0; i < 10; i++) {
            String dummy = "" + i;
            regestry.addIPlug(new DummyIPlug(dummy, new String[] { dummy }));
        }
        for (int i = 0; i < 10; i++) {
            String dummy = "" + i;
            QueryStringParser parser = new QueryStringParser(new StringReader(dummy + ":" + dummy));
            IngridQuery query = parser.parse();
            IIPlug[] plugsForQuery = regestry.getIPlugsForQuery(query);
            assertEquals(1, plugsForQuery.length);
        }

        QueryStringParser parser = new QueryStringParser(new StringReader("a simple Query"));
        IngridQuery query = parser.parse();
        assertEquals(10, regestry.getIPlugsForQuery(query).length);
    }

}
