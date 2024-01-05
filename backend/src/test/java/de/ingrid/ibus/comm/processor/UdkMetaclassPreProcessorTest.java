/*
 * **************************************************-
 * InGrid iBus
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
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

package de.ingrid.ibus.comm.processor;

import de.ingrid.ibus.comm.processor.UdkMetaclassPreProcessor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * Test for {@link de.ingrid.ibus.comm.processor.UdkMetaclassPreProcessor}.
 * 
 * <p/>created on 19.05.2006
 * 
 * @version $Revision: $
 * @author jz
 * @author $Author: ${lastedit}
 * 
 */
public class UdkMetaclassPreProcessorTest {

    /**
     * @throws Exception
     */
    @Test
    public void testProcess() throws Exception {
        IngridQuery query = QueryStringParser.parse("query " + UdkMetaclassPreProcessor.PORTAL_METACLASS + ':'
                + UdkMetaclassPreProcessor.PORTAL_METACLASS_DATABASE + ' ' + UdkMetaclassPreProcessor.PORTAL_METACLASS
                + ':' + UdkMetaclassPreProcessor.PORTAL_METACLASS_DATABASE);
        new UdkMetaclassPreProcessor().process(query);

        assertTrue(query.containsField(UdkMetaclassPreProcessor.UDK_METACLASS));
        assertEquals(UdkMetaclassPreProcessor.UDK_METACLASS_DATABASE, query.getFields()[0].getFieldValue());

        query.removeField(UdkMetaclassPreProcessor.UDK_METACLASS);
        assertTrue(query.containsField(UdkMetaclassPreProcessor.UDK_METACLASS));
        assertEquals(UdkMetaclassPreProcessor.UDK_METACLASS_DATABASE, query.getFields()[0].getFieldValue());

        query.removeField(UdkMetaclassPreProcessor.UDK_METACLASS);
        assertFalse(query.containsField(UdkMetaclassPreProcessor.UDK_METACLASS));
    }

    /**
     * @throws Exception
     */
    @Test
    public void testProcessClauses() throws Exception {
        IngridQuery query = QueryStringParser.parse("(query " + UdkMetaclassPreProcessor.PORTAL_METACLASS + ':'
                + UdkMetaclassPreProcessor.PORTAL_METACLASS_DATABASE + " " + UdkMetaclassPreProcessor.PORTAL_METACLASS
                + ':' + UdkMetaclassPreProcessor.PORTAL_METACLASS_DATABASE + " )");
        new UdkMetaclassPreProcessor().process(query);

        assertTrue(query.getClauses()[0].containsField(UdkMetaclassPreProcessor.UDK_METACLASS));
        assertEquals(UdkMetaclassPreProcessor.UDK_METACLASS_DATABASE, query.getClauses()[0].getFields()[0]
                .getFieldValue());
        
        query.getClauses()[0].removeField(UdkMetaclassPreProcessor.UDK_METACLASS);
        assertTrue(query.getClauses()[0].containsField(UdkMetaclassPreProcessor.UDK_METACLASS));
        assertEquals(UdkMetaclassPreProcessor.UDK_METACLASS_DATABASE, query.getClauses()[0].getFields()[0]
                .getFieldValue());

        query.getClauses()[0].removeField(UdkMetaclassPreProcessor.UDK_METACLASS);
        assertFalse(query.getClauses()[0].containsField(UdkMetaclassPreProcessor.UDK_METACLASS));
    }

}
