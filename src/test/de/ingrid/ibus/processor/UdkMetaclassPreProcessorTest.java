/*
 * Copyright (c) 1997-2006 by media style GmbH
 */

package de.ingrid.ibus.processor;

import junit.framework.TestCase;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * Test for {@link de.ingrid.ibus.processor.UdkMetaclassPreProcessor}.
 * 
 * <p/>created on 19.05.2006
 * 
 * @version $Revision: $
 * @author jz
 * @author $Author: ${lastedit}
 * 
 */
public class UdkMetaclassPreProcessorTest extends TestCase {

    /**
     * @throws Exception
     */
    public void testProcess() throws Exception {
        IngridQuery query = QueryStringParser.parse("query " + UdkMetaclassPreProcessor.PORTAL_METACLASS + ':'
                + UdkMetaclassPreProcessor.PORTAL_METACLASS_DATABASE);
        new UdkMetaclassPreProcessor().process(query);
        assertTrue(query.containsField(UdkMetaclassPreProcessor.UDK_METACLASS));
        assertEquals(UdkMetaclassPreProcessor.UDK_METACLASS_DATABASE, query.getFields()[0].getFieldValue());
    }

    /**
     * @throws Exception
     */
    public void testProcessClauses() throws Exception {
        IngridQuery query = QueryStringParser.parse("(query " + UdkMetaclassPreProcessor.PORTAL_METACLASS + ':'
                + UdkMetaclassPreProcessor.PORTAL_METACLASS_DATABASE + " )");
        new UdkMetaclassPreProcessor().process(query);
        assertTrue(query.getClauses()[0].containsField(UdkMetaclassPreProcessor.UDK_METACLASS));
        assertEquals(UdkMetaclassPreProcessor.UDK_METACLASS_DATABASE, query.getClauses()[0].getFields()[0]
                .getFieldValue());
    }

}
