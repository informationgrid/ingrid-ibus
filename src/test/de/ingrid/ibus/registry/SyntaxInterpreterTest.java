/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.registry;

import java.io.StringReader;

import junit.framework.TestCase;
import de.ingrid.iplug.PlugDescription;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.query.TermQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;

public class SyntaxInterpreterTest extends TestCase {

    /**
     * test for INGRID-36
     * @throws ParseException
     */
    
    public void testParseQuery() throws ParseException{
        testSimple();
        testFieldQuery();
        testBooleanQuery();
        
    }
    
    public void testSimple() throws ParseException{
        IngridQuery query = QueryStringParser.parse("halle saale");
        boolean containsHalle = false;
        boolean containsSaale = false;
        TermQuery[] terms = query.getTerms();
        for (int i = 0; i < terms.length; i++) {
            if("halle".equals(terms[i].getTerm())){
                containsHalle = true;
            }
            if("saale".equals(terms[i].getTerm())){
                containsSaale = true;
            }
        }
        assertTrue(containsHalle);
        assertTrue(containsSaale);
    }
    
    public void testFieldQuery() throws ParseException{
        IngridQuery query = QueryStringParser.parse("field1:value1 field2:value2 term");
        FieldQuery[] fields = query.getFields();
        for (int i = 1; i <= fields.length; i++) {
            FieldQuery field = fields[i-1];
            assertEquals("field" + i, field.getFieldName());
            assertEquals("value" + i, field.getFieldValue());
        }
    }
    
    public void testBooleanQuery() throws ParseException, de.ingrid.utils.queryparser.ParseException{
        IngridQuery query = QueryStringParser.parse("term1 OR term2");
        assertEquals(2, query.getTerms().length);
        assertEquals(IngridQuery.OR, query.getTerms()[1].getOperation());
    }
    /**
     * 
     * @throws Exception
     */
    public void testGetIplugsForQuery() throws Exception {
        Regestry regestry = new Regestry(1000000);
        for (int i = 0; i < 10; i++) {
            String dummy = "" + i;
            PlugDescription description = new PlugDescription();
            description.addField(dummy);
            description.setPlugId(dummy);
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
