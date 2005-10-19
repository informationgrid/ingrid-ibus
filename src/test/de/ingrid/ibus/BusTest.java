/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import junit.framework.TestCase;
import de.ingrid.iplug.PlugDescription;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * BusTest
 * 
 * <p/>created on 07.09.2005
 * 
 * @version $Revision: $
 * @author sg
 * @author $Author jz ${lastedit}
 * 
 */
public class BusTest extends TestCase {

    /**
     * @throws Exception
     */
    public void testSendQueries() throws Exception {
        Bus bus = new Bus(new DummyProxyFactory());

        for (int i = 0; i < 3; i++) {
            PlugDescription plug = new PlugDescription();
            plug.setPlugId("" + i);
            bus.getIPlugRegestry().addIPlug(plug);
        }

        IngridQuery query = QueryStringParser.parse("fische ort:halle");
        IngridDocument[] documents = bus.search(query, 10, 1, Integer.MAX_VALUE, 1000);
        assertEquals(3, documents.length);

    }

}
