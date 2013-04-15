package de.ingrid.ibus.processor;

import junit.framework.TestCase;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;
import de.ingrid.utils.tool.QueryUtil;

public class QueryModifierPreProcessorTest extends TestCase {

    public void testProcess() throws Exception {
        QueryModifierPreProcessor modifier = new QueryModifierPreProcessor("/querymodifier.properties");

        IngridQuery q = QueryStringParser.parse("wasser Elbhochwasser cola bier (drink:cola OR drink:water -(oft \"nicht oft\"))");

        System.out.println("before: " + QueryUtil.query2String(q));
        modifier.process(q);
        System.out.println("after: " + QueryUtil.query2String(q));
    }

}
