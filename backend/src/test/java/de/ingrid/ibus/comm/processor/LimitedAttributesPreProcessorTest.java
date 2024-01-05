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

import de.ingrid.ibus.comm.processor.LimitedAttributesPreProcessor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * Test for {@link de.ingrid.ibus.comm.processor.LimitedAttributesPreProcessor}.
 * 
 * <p/>created on 31.05.2006
 * 
 * @version $Revision: $
 * @author jz
 * @author $Author: ${lastedit}
 * 
 */
public class LimitedAttributesPreProcessorTest {

    /**
     * @throws Exception
     */
    @Test
    public void testIt() throws Exception {
        LimitedAttributesPreProcessor preProcessor = new LimitedAttributesPreProcessor();
        IngridQuery query = QueryStringParser.parse("wasser AND (brot OR wein) "
                + LimitedAttributesPreProcessor.ATTRIBUTE_RANGE + ":"
                + LimitedAttributesPreProcessor.ATTRIBUTE_RANGE_LIMITED);

        assertEquals(1, query.getTerms().length);
        assertEquals(1, query.getClauses().length);
        assertEquals(2, query.getClauses()[0].getTerms().length);
        preProcessor.process(query);

        // check terms and clauses
        assertEquals(0, query.getTerms().length);
        assertEquals(2, query.getClauses().length);
        assertEquals(0, query.getClauses()[0].getTerms().length);
        assertEquals(2, query.getClauses()[0].getClauses().length);

        // check fields
        assertEquals(5, query.getClauses()[1].getFields().length);
        assertEquals(5, query.getClauses()[0].getClauses()[0].getFields().length);
        assertEquals(5, query.getClauses()[0].getClauses()[1].getFields().length);
    }
}
