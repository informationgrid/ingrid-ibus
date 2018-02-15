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
 * Copyright (c) 1997-2006 by media style GmbH
 */

package de.ingrid.ibus.processor;

import junit.framework.TestCase;
import de.ingrid.ibus.comm.processor.AddressPreProcessor;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * Test for {@link de.ingrid.ibus.comm.processor.AddressPreProcessor}.
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
    public void testProcessZip() throws Exception {
        IngridQuery query = QueryStringParser.parse("query zip:282");
        new AddressPreProcessor().process(query);
        // assertFalse(query.containsField(AddressPreProcessor.ZIP));
        assertTrue(query.getClauses()[0].containsField(AddressPreProcessor.ZIP));
        assertTrue(query.getClauses()[0].containsField(AddressPreProcessor.ZIP_UDK_NAME2));
        assertEquals("282", query.getClauses()[0].getFields()[0].getFieldValue());

        query = QueryStringParser.parse("query (ad zip:282)");
        new AddressPreProcessor().process(query);
        // assertFalse(query.getClauses()[0].getClauses()[0].containsField(AddressPreProcessor.ZIP));
        assertTrue(query.getClauses()[0].getClauses()[0].containsField(AddressPreProcessor.ZIP));
        assertTrue(query.getClauses()[0].getClauses()[0].containsField(AddressPreProcessor.ZIP_UDK_NAME2));
        assertEquals("282", query.getClauses()[0].getClauses()[0].getFields()[0].getFieldValue());
    }

    // /**
    // * @throws Exception
    // */
    // public void testProcessStreet() throws Exception {
    // IngridQuery query = QueryStringParser.parse("query street:aStreet");
    // new AddressPreProcessor().process(query);
    // assertFalse(query.containsField(AddressPreProcessor.STREET));
    // assertTrue(query.containsField(AddressPreProcessor.STREET_UDK_NAME));
    // assertEquals("aStreet", query.getFields()[0].getFieldValue());
    // }
    //    
    // /**
    // * @throws Exception
    // */
    // public void testProcessCity() throws Exception {
    // IngridQuery query = QueryStringParser.parse("query city:cityOfGod");
    // new AddressPreProcessor().process(query);
    // assertFalse(query.containsField(AddressPreProcessor.CITY));
    // assertTrue(query.containsField(AddressPreProcessor.CITY_UDK_NAME));
    // assertEquals("cityOfGod", query.getFields()[0].getFieldValue());
    // }
}
