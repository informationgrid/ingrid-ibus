/*
 * **************************************************-
 * InGrid iBus
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: DispatcherTest.java,v $
 */
package de.ingrid.comm.v01;

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
        new de.ingrid.comm.registry.SyntaxInterpreterTest().testGetIPlugs_DataTypes();
        new de.ingrid.comm.registry.SyntaxInterpreterTest().testGetIplugs_FieldsAndDataTypes();
    }

    /**
     * test for feature INGRID-38
     * 
     * @throws Exception
     */
    public void testFindDataFields() throws Exception {
        new de.ingrid.comm.registry.SyntaxInterpreterTest().testGetIplugs_Fields();
        new de.ingrid.comm.registry.SyntaxInterpreterTest().testGetIplugs_FieldsAndDataTypes();
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
        assertEquals(false, query.getTerms()[1].isRequred());
    }
}
