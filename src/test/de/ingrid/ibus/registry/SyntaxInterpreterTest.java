/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.registry;

import java.io.StringReader;

import junit.framework.TestCase;
import de.ingrid.iplug.IIPlug;
import de.ingrid.utils.FieldQuery;
import de.ingrid.utils.IngridQuery;
import de.ingrid.utils.TermQuery;
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
    
    private void testSimple() throws ParseException{
        IngridQuery query = SyntaxInterpreter.parseQuery("halle saale");
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
    
    private void testFieldQuery() throws ParseException{
        IngridQuery query = SyntaxInterpreter.parseQuery("field1:value1 field2:value2 term");
        FieldQuery[] fields = query.getFields();
        for (int i = 1; i <= fields.length; i++) {
            FieldQuery field = fields[i-1];
            assertEquals("field" + i, field.getFieldName());
            assertEquals("value" + i, field.getFieldValue());
        }
    }
    
    private void testBooleanQuery() throws ParseException{
        IngridQuery query = SyntaxInterpreter.parseQuery("term1 OR term2");
        assertEquals(2, query.getTerms().length);
        assertEquals(IngridQuery.OR, query.getTerms()[1].getOperation());
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
            IIPlug[] plugsForQuery = SyntaxInterpreter.getIPlugsForQuery(query, regestry);
            assertEquals(1, plugsForQuery.length);
        }
    
        QueryStringParser parser = new QueryStringParser(new StringReader("a simple Query"));
        IngridQuery query = parser.parse();
        assertEquals(10,  SyntaxInterpreter.getIPlugsForQuery(query, regestry).length);
    }

}
