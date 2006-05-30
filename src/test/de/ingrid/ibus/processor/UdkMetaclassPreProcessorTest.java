/*
 * Copyright 2004-2005 weta group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 *  $Source:  $
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
     * 
     */
    public void testProcess() throws Exception {
        IngridQuery query = QueryStringParser.parse("query "+UdkMetaclassPreProcessor.PORTAL_METACLASS+":"+UdkMetaclassPreProcessor.PORTAL_METACLASS_DATABASE);
        new UdkMetaclassPreProcessor().process(query);
        assertFalse(query.containsField(UdkMetaclassPreProcessor.PORTAL_METACLASS));
        assertTrue(query.containsField(UdkMetaclassPreProcessor.UDK_METACLASS));
        assertEquals(UdkMetaclassPreProcessor.UDK_METACLASS_DATABASE,query.getFields()[0].getFieldValue());
    }
    
    /**
     * @throws Exception
     */
    public void testProcessClauses() throws Exception {
        IngridQuery query = QueryStringParser.parse("(query "+UdkMetaclassPreProcessor.PORTAL_METACLASS+":"+UdkMetaclassPreProcessor.PORTAL_METACLASS_DATABASE+" )");
        new UdkMetaclassPreProcessor().process(query);
        assertFalse(query.containsField(UdkMetaclassPreProcessor.PORTAL_METACLASS));
        assertTrue(query.containsField(UdkMetaclassPreProcessor.UDK_METACLASS));
        assertEquals(UdkMetaclassPreProcessor.UDK_METACLASS_DATABASE,query.getFields()[0].getFieldValue());
    }

}
