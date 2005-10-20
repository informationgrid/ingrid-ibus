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

/**
 * SyntaxInterpreterTest
 * 
 * <p/>created on 07.09.2005
 * 
 * @version $Revision: $
 * @author sg
 * @author $Author: ${lastedit}
 * 
 */
public class SyntaxInterpreterTest extends TestCase {

    /**
     * 
     * @throws Exception
     */
    public void testGetIplugsForQuery() throws Exception {
        Registry registry = new Registry(1000000);
        for (int i = 0; i < 10; i++) {
            String dummy = "" + i;
            PlugDescription description = new PlugDescription();
            description.addField(dummy);
            description.setPlugId(dummy);
            registry.addIPlug(description);
        }
        for (int i = 0; i < 10; i++) {
            String dummy = "" + i;
            QueryStringParser parser = new QueryStringParser(new StringReader(dummy + ":" + dummy));
            IngridQuery query = parser.parse();
            PlugDescription[] plugsForQuery = SyntaxInterpreter.getIPlugsForQuery(query, registry);
            assertEquals(1, plugsForQuery.length);
        }

        QueryStringParser parser = new QueryStringParser(new StringReader("a simple Query"));
        IngridQuery query = parser.parse();
        assertEquals(10, SyntaxInterpreter.getIPlugsForQuery(query, registry).length);
    }

    /**
     * @throws Exception
     */
    public void testGetIplugsForQuery_Fields() throws Exception {
        // TODO implement
    }

    /**
     * @throws Exception
     */
    public void testGetIplugsForQuery_DataTypes() throws Exception {
        // TODO implement

    }

    /**
     * @throws Exception
     */
    public void testGetIplugsForQuery_FieldsAndDataTypes() throws Exception {
        // TODO implement
    }
}
