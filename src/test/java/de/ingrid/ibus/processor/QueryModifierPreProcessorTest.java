package de.ingrid.ibus.processor;

import junit.framework.TestCase;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;
import de.ingrid.utils.tool.QueryUtil;

public class QueryModifierPreProcessorTest extends TestCase {

    public void testProcess() throws Exception {
        QueryModifierPreProcessor modifier = new QueryModifierPreProcessor("/querymodifier.properties");

        IngridQuery q = QueryStringParser.parse("wasser Havelhochwasser cola bier (drink:cola OR drink:water -(oft \"nicht oft\"))");

        System.out.println("before: " + QueryUtil.query2String(q));
        modifier.process(q);
        System.out.println("after: " + QueryUtil.query2String(q));
        
        IngridQuery q1 = QueryStringParser.parse("Elbhochwasser OR (10 Jahre Elbehochwasser) OR (10 Jahre Elbe-Hochwasser) OR (Elbe-Hochwasser 2002) OR Elbehochwasser");
        IngridQuery q2 = QueryStringParser.parse("Elbhochwasser");
        modifier.process(q2);
        System.out.println("query without modifier: " + QueryUtil.query2String(q1));
        System.out.println("query with    modifier: " + QueryUtil.query2String(q2));
        
        
        
    }

}
