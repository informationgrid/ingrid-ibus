/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import junit.framework.TestCase;
import de.ingrid.ibus.registry.DummyIPlug;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

public class BusTest extends TestCase {

    public void testSendQueries() throws Exception {
        Bus bus = new Bus(new DummyProxyFactory());
        
        for (int i = 0; i < 3; i++) {
            DummyIPlug plug = new DummyIPlug("" + i, new String[] { "ort" });
            bus.getIPlugRegestry().addIPlug(plug);
        }

        IngridQuery query = QueryStringParser.parse("fische ort:halle");
        IngridDocument[] documents = bus.search(query, Integer.MAX_VALUE, 1000);
        assertEquals(3, documents.length);

    }

}
