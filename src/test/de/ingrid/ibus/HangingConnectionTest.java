/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import junit.framework.TestCase;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.queryparser.QueryStringParser;

public class HangingConnectionTest extends TestCase {

    public void testhangConnection() throws Exception {
        Bus bus = new Bus(new HangingPlugDummyProxyFactory());
        PlugDescription plugDescriptions = new PlugDescription();
        plugDescriptions.setPlugId("");
        plugDescriptions.setOrganisation("org");
        bus.getIPlugRegistry().addIPlug(plugDescriptions);
        long start = System.currentTimeMillis();
        bus.search(QueryStringParser.parse("hallo"), 10, 1, 100, 1000);
        assertTrue(start + 100 < System.currentTimeMillis());
    }

}
