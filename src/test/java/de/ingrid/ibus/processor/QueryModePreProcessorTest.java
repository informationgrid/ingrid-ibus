/*
 * Copyright (c) 1997-2006 by media style GmbH
 */

package de.ingrid.ibus.processor;

import junit.framework.TestCase;
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
public class QueryModePreProcessorTest extends TestCase {

    /**
     * @throws Exception
     */
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
