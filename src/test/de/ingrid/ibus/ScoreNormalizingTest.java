/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import java.util.HashMap;

import de.ingrid.ibus.registry.Registry;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;
import junit.framework.TestCase;

/**
 * TODO comment for ScoreNormalizingTest
 * 
 * <p/>created on 29.04.2006
 * 
 * @version $Revision: $
 * @author jz
 * @author $Author: ${lastedit}
 * 
 */
public class ScoreNormalizingTest extends TestCase {

    /**
     * @throws Exception
     */
    public void testScore() throws Exception {
        Bus bus = new Bus(new DummyProxyFactory());
        Registry registry = bus.getIPlugRegistry();

        PlugDescription plugDescriptions0 = new PlugDescription();
        plugDescriptions0.setProxyServiceURL("1");
        plugDescriptions0.setOrganisation("friedens ministerium");
        registry.addIPlug(plugDescriptions0);

        PlugDescription plugDescriptions1 = new PlugDescription();
        plugDescriptions1.setProxyServiceURL("2");
        plugDescriptions1.setOrganisation("liebes ministerium"); // 1984
        registry.addIPlug(plugDescriptions1);

        HashMap globalRanking = new HashMap();
        globalRanking.put("2", new Float(0.1111));
        globalRanking.put("1", new Float(0.111));
        registry.setGlobalRanking(globalRanking);

        IngridQuery query = QueryStringParser.parse("a Query");
        IngridHits hits = bus.search(query, 10, 0, 100, 1000);
        IngridHit[] hitsArray = hits.getHits();
        for (int i = 0; i < hitsArray.length; i++) {
            IngridHit hit = hitsArray[i];
            System.out.println("plugid:" + hit.getPlugId());
            assertTrue(hit.getScore() <= 1.0f);
        }
    }
}
