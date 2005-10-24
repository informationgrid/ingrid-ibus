/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: DispatcherTest.java,v $
 */
package de.ingrid.ibus.v01;

import junit.framework.TestCase;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.query.TermQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * Test for syntax-interpreter-feature (INGRID-1). created on 21.07.2005
 * <p>
 * 
 * @author hs
 */
public class SyntaxInterpreterTest extends TestCase {
    private static de.ingrid.ibus.registry.SyntaxInterpreterTest interpreterTest = new de.ingrid.ibus.registry.SyntaxInterpreterTest();

    /**
     * test for feature INGRID-36
     * 
     * @throws Exception
     */
    public void testTranslateToQuerySyntax() throws Exception {
        // tests functionality from QueryStringParser from ingrid-utils
        checkParseSimple();
        checkParseFieldQuery();
        checkParseBooleanQuery();
    }

    /**
     * test for feature INGRID-37
     * 
     * @throws Exception
     */
    public void testFindAddressedDataSources() throws Exception {
        interpreterTest.testGetIPlugs_DataTypes();
        interpreterTest.testGetIplugs_FieldsAndDataTypes();
    }

    /**
     * test for feature INGRID-38
     * 
     * @throws Exception
     */
    public void testFindDataFields() throws Exception {
        interpreterTest.testGetIplugs_Fields();
        interpreterTest.testGetIplugs_FieldsAndDataTypes();
    }

    // ------------private------------------

    private void checkParseSimple() throws ParseException {
        IngridQuery query = QueryStringParser.parse("halle saale");
        boolean containsHalle = false;
        boolean containsSaale = false;
        TermQuery[] terms = query.getTerms();
        for (int i = 0; i < terms.length; i++) {
            if ("halle".equals(terms[i].getTerm())) {
                containsHalle = true;
            }
            if ("saale".equals(terms[i].getTerm())) {
                containsSaale = true;
            }
        }
        assertTrue(containsHalle);
        assertTrue(containsSaale);
    }

    private void checkParseFieldQuery() throws ParseException {
        IngridQuery query = QueryStringParser.parse("field1:value1 field2:value2 term");
        FieldQuery[] fields = query.getFields();
        for (int i = 1; i <= fields.length; i++) {
            FieldQuery field = fields[i - 1];
            assertEquals("field" + i, field.getFieldName());
            assertEquals("value" + i, field.getFieldValue());
        }
    }

    private void checkParseBooleanQuery() throws ParseException, de.ingrid.utils.queryparser.ParseException {
        IngridQuery query = QueryStringParser.parse("term1 OR term2");
        assertEquals(2, query.getTerms().length);
        assertEquals(IngridQuery.OR, query.getTerms()[1].getOperation());
    }
}
