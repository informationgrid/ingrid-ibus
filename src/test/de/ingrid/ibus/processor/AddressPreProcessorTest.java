/*
 * Copyright (c) 1997-2006 by media style GmbH
 */

package de.ingrid.ibus.processor;

import junit.framework.TestCase;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * Test for {@link de.ingrid.ibus.processor.AddressPreProcessor}.
 * 
 * <p/>created on 01.06.2006
 * 
 * @version $Revision: $
 * @author jz
 * @author $Author: ${lastedit}
 * 
 */
public class AddressPreProcessorTest extends TestCase {

    /**
     * @throws Exception
     */
    public void testProcessip() throws Exception {
        IngridQuery query = QueryStringParser.parse("query zip:282");
        new AddressPreProcessor().process(query);
        assertFalse(query.containsField(AddressPreProcessor.ZIP));
        assertTrue(query.containsField(AddressPreProcessor.ZIP_UDK_NAME1));
        assertTrue(query.containsField(AddressPreProcessor.ZIP_UDK_NAME2));
        assertEquals("282", query.getFields()[0].getFieldValue());
        
        query =QueryStringParser.parse("query (ad zip:282)");
        new AddressPreProcessor().process(query);
        assertFalse(query.getClauses()[0].containsField(AddressPreProcessor.ZIP));
        assertTrue(query.getClauses()[0].containsField(AddressPreProcessor.ZIP_UDK_NAME1));
        assertTrue(query.getClauses()[0].containsField(AddressPreProcessor.ZIP_UDK_NAME2));
        assertEquals("282", query.getClauses()[0].getFields()[0].getFieldValue());
    }
    
    /**
     * @throws Exception
     */
    public void testProcessStreet() throws Exception {
        IngridQuery query = QueryStringParser.parse("query street:aStreet");
        new AddressPreProcessor().process(query);
        assertFalse(query.containsField(AddressPreProcessor.STREET));
        assertTrue(query.containsField(AddressPreProcessor.STREET_UDK_NAME));
        assertEquals("aStreet", query.getFields()[0].getFieldValue());
    }
    
    /**
     * @throws Exception
     */
    public void testProcessCity() throws Exception {
        IngridQuery query = QueryStringParser.parse("query city:cityOfGod");
        new AddressPreProcessor().process(query);
        assertFalse(query.containsField(AddressPreProcessor.CITY));
        assertTrue(query.containsField(AddressPreProcessor.CITY_UDK_NAME));
        assertEquals("cityOfGod", query.getFields()[0].getFieldValue());
    }
}
