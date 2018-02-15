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
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import junit.framework.TestCase;
import de.ingrid.ibus.comm.Bus;
import de.ingrid.ibus.comm.registry.Registry;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

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

    private Bus setUp(float[][] scores) {
        Bus bus = new Bus(new DummyProxyFactory(scores));
        Registry registry = bus.getIPlugRegistry();
        registry.setCommunication(new DummyCommunication());

        PlugDescription plugDescriptions1 = new PlugDescription();
        plugDescriptions1.setProxyServiceURL("/:2");
        plugDescriptions1.setOrganisation("liebes ministerium"); // 1984
        registry.addPlugDescription(plugDescriptions1);
        registry.activatePlug("/:2");

        PlugDescription plugDescriptions0 = new PlugDescription();
        plugDescriptions0.setProxyServiceURL("/:1");
        plugDescriptions0.setOrganisation("friedens ministerium");
        registry.addPlugDescription(plugDescriptions0);
        registry.activatePlug("/:1");


//        HashMap<String, Float> globalRanking = new HashMap<String, Float>();
//        globalRanking.put("/:2", new Float(0.1111));
//        globalRanking.put("/:1", new Float(0.111));
//        registry.setGlobalRanking(globalRanking);
        return bus;
    }
  
    /**
     * @throws Exception
     */
    public void testScore() throws Exception {
        System.out.println("testScore");
        // scores have to be sorted in descending order (like from a real iPlug)
        float[][] scores = {{2.5f, 0.5f},{0.5f, 0.31f}};
        float[] expectedScores = {1.0f, 0.5f, 0.31f, 0.2f};
        
        Bus bus = setUp(scores);
        
        IngridQuery query = QueryStringParser.parse("a Query ranking:score");
        IngridHits hits = bus.search(query, 10, 0, 100, 1000);
        IngridHit[] hitsArray = hits.getHits();
        for (int i = 0; i < hitsArray.length; i++) {
            IngridHit hit = hitsArray[i];
            System.out.println("plugid:" + hit.getPlugId() + " score: " + hit.getScore());
            assertTrue(hit.getScore() == expectedScores[i]);
        }
    }
    
    public void testScoreAllBelowOne() throws Exception {
        System.out.println("testScoreAllBelowOne");
        // scores have to be sorted in descending order (like from a real iPlug)
        float[][] scores = {{0.8f, 0.5f},{0.9f, 0.61f}};
        float[] expectedScores = {0.9f, 0.8f, 0.61f, 0.5f};
        
        Bus bus = setUp(scores);
        
        IngridQuery query = QueryStringParser.parse("a Query ranking:score");
        IngridHits hits = bus.search(query, 10, 0, 100, 1000);
        IngridHit[] hitsArray = hits.getHits();
        for (int i = 0; i < hitsArray.length; i++) {
            IngridHit hit = hitsArray[i];
            System.out.println("plugid:" + hit.getPlugId() + " score: " + hit.getScore());
            assertTrue(hit.getScore() == expectedScores[i]);
        }
    }
    
    public void testScoreAllAboveOne() throws Exception {
        System.out.println("testScoreAllAboveOne");
        // scores have to be sorted in descending order (like from a real iPlug)
        float[][] scores = {{2.0f, 1.5f},{1.25f, 1.1f}};
        float[] expectedScores = {1.0f, 1.0f, 0.88f, 0.75f};
        
        Bus bus = setUp(scores);
        
        IngridQuery query = QueryStringParser.parse("a Query ranking:score");
        IngridHits hits = bus.search(query, 10, 0, 100, 1000);
        IngridHit[] hitsArray = hits.getHits();
        for (int i = 0; i < hitsArray.length; i++) {
            IngridHit hit = hitsArray[i];
            System.out.println("plugid:" + hit.getPlugId() + " score: " + hit.getScore());
            assertTrue(hit.getScore() == expectedScores[i]);
        }
    }
   
    public void testScorePaging() throws Exception {
        System.out.println("testScoreMoreResultsAvailable");
        // scores have to be sorted in descending order (like from a real iPlug)
        float[][] scores = {
                {6.0f, 4.5f, 3.75f, 3.0f, 1.5f, 0.75f, 0.5f, 0.4f, 0.3f},
                {5.0f, 0.8f, 0.7f, 0.5f, 0.45f, 0.4f, 0.3f, 0.2f, 0.1f}
        };
        float[] expectedScores1 = {1.0f, 1.0f, 0.75f, 0.625f, 0.5f};
        float[] expectedScores2 = {0.25f, 0.16f, 0.14f, 0.125f, 0.1f};
        
        Bus bus = setUp(scores);
        
        IngridQuery query = QueryStringParser.parse("a Query ranking:score");
        
        IngridHits hits = bus.search(query, 5, 1, 0, 1000);
        IngridHit[] hitsArray = hits.getHits();
        
        IngridHits hits2 = bus.search(query, 5, 2, 0, 1000);
        IngridHit[] hitsArray2 = hits2.getHits();
        
        for (int i = 0; i < hitsArray.length; i++) {
            System.out.println("plugid:" + hitsArray[i].getPlugId() + " score: " + hitsArray[i].getScore());
            assertTrue(hitsArray[i].getScore() == expectedScores1[i]);
        }
        for (int i = 0; i < hitsArray2.length; i++) {
            System.out.println("plugid:" + hitsArray2[i].getPlugId() + " score: " + hitsArray2[i].getScore());
            assertTrue(hitsArray2[i].getScore() == expectedScores2[i]);
        }
    }
    
    public void testScoreOneIPlugOnly() throws Exception {
        float[][] scores = {
                {6.0f, 4.5f, 3.75f, 3.0f, 1.5f, 0.75f, 0.5f, 0.4f, 0.3f},
                {5.0f, 0.8f, 0.7f, 0.5f, 0.45f, 0.4f, 0.3f, 0.2f, 0.1f}
        };
        float[] expectedScores1 = {6.0f, 4.5f, 3.75f, 3.0f, 1.5f, 0.75f, 0.5f, 0.4f, 0.3f};
        
        Bus bus = setUp(scores);
        bus.getIPlugRegistry().removePlug("/:1");
        
        IngridQuery query = QueryStringParser.parse("a Query ranking:score");
        
        IngridHits hits = bus.search(query, 5, 1, 0, 1000);
        IngridHit[] hitsArray = hits.getHits();
        
        for (int i = 0; i < hitsArray.length; i++) {
            System.out.println("plugid:" + hitsArray[i].getPlugId() + " score: " + hitsArray[i].getScore());
            assertTrue(hitsArray[i].getScore() == expectedScores1[i]);
        }
    }
    
    public void testScoreSameScoreGroupedIPlugs() throws Exception {
        System.out.println("testScoreSameScoreGroupedIPlugs");
        // scores have to be sorted according to plug IDs
        float[][] scores = {{1.0f, 1.0f},{1.0f, 1.0f}};
        float[] expectedScores = {1.0f, 1.0f, 1.0f, 1.0f};
        
        Bus bus = setUp(scores);
        
        IngridQuery query = QueryStringParser.parse("a Query ranking:score");
        IngridHits hits = bus.search(query, 10, 0, 100, 1000);
        IngridHit[] hitsArray = hits.getHits();
        for (int i = 0; i < hitsArray.length; i++) {
            IngridHit hit = hitsArray[i];
            System.out.println("plugid:" + hit.getPlugId() + " score: " + hit.getScore());
            assertTrue(hit.getScore() == expectedScores[i]);
            if (i<2) {
                assertTrue(hit.getPlugId().equals("/:1"));
            } else {
                assertTrue(hit.getPlugId().equals("/:2"));
            }
        }
    }
    
}
