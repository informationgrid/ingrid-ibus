package de.ingrid.ibus.processor;

import junit.framework.TestCase;

import org.apache.commons.configuration.PropertiesConfiguration;

import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;
import de.ingrid.utils.tool.QueryUtil;

public class QueryModifierPreProcessorTest extends TestCase {

    public void testProcess() throws Exception {
        QueryModifierPreProcessor modifier = new QueryModifierPreProcessor();

        PropertiesConfiguration config = new PropertiesConfiguration();
        config.addProperty("wasser", "wasser OR boden");
        config.addProperty("cola", "fanta");
        config.addProperty("drink:cola", "drink:fanta");
        config.addProperty("cola", "fanta");
        config.addProperty("nicht oft", "-oft");
        config.addProperty("oft", "-(selten -nie)");

        modifier.config = config;

        IngridQuery q = QueryStringParser.parse("wasser cola bier (drink:cola OR drink:water -(oft \"nicht oft\"))");

        System.out.println("before: " + QueryUtil.query2String(q));
        modifier.process(q);
        System.out.println("after: " + QueryUtil.query2String(q));
    }

}
