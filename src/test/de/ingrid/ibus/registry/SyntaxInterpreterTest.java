/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.registry;

import java.io.StringReader;

import junit.framework.TestCase;
import de.ingrid.iplug.PlugDescription;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

public class SyntaxInterpreterTest extends TestCase {

    /**
     * 
     * @throws Exception
     */
    public void testGetIplugsForQuery() throws Exception {
        Regestry regestry = new Regestry();
        for (int i = 0; i < 10; i++) {
            String dummy = "" + i;
            PlugDescription description = new PlugDescription();
            description.addField(dummy);
            regestry.addIPlug(description);
        }
        for (int i = 0; i < 10; i++) {
            String dummy = "" + i;
            QueryStringParser parser = new QueryStringParser(new StringReader(dummy + ":" + dummy));
            IngridQuery query = parser.parse();
            PlugDescription[] plugsForQuery = SyntaxInterpreter.getIPlugsForQuery(query, regestry);
            assertEquals(1, plugsForQuery.length);
        }

        QueryStringParser parser = new QueryStringParser(new StringReader("a simple Query"));
        IngridQuery query = parser.parse();
        assertEquals(10, SyntaxInterpreter.getIPlugsForQuery(query, regestry).length);
    }

}
