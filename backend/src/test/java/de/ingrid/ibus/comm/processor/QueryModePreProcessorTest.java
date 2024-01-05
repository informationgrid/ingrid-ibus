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

import de.ingrid.ibus.comm.processor.QueryModePreProcessor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * Test for {@link QueryModePreProcessor}.
 * 
 * <p/>created on 01.06.2006
 * 
 * @version $Revision: $
 * @author jz
 * @author $Author: ${lastedit}
 * 
 */
public class QueryModePreProcessorTest {

    /**
     * @throws Exception
     */
    @Test
    public void testProcessQuerymodeSubstring() throws Exception {
        IngridQuery query = QueryStringParser.parse("query (clauseQuery1 OR clauseQuery2)");
        query.addField(new FieldQuery(true, false, QueryModePreProcessor.QUERYMODE,
                QueryModePreProcessor.QUERYMODE_SUBSTRING));
        assertEquals(1, query.getTerms().length);
        assertEquals(2, query.getClauses()[0].getTerms().length);
        QueryModePreProcessor preProcessor = new QueryModePreProcessor();
        preProcessor.process(query);
        assertEquals(1, query.getTerms().length);
        assertEquals("query*", query.getTerms()[0].getTerm());
        assertEquals(2, query.getClauses()[0].getTerms().length);
        assertEquals("clauseQuery1*", query.getClauses()[0].getTerms()[0].getTerm());
        assertEquals("clauseQuery2*", query.getClauses()[0].getTerms()[1].getTerm());

    }
}
