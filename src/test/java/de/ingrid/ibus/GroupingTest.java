/*
 * **************************************************-
 * InGrid iBus
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
package de.ingrid.ibus;

import java.util.Iterator;

import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;
import junit.framework.TestCase;

/**
 * Test for grouping functionality.
 * 
 * <p/>created on 29.04.2006
 * 
 * @version $Revision: $
 * @author jz
 * @author $Author: ${lastedit}
 * 
 */
public class GroupingTest extends TestCase {

    private static final String ORGANISATION = "a organisation";

    private Bus fBus;

    private PlugDescription[] plugDescriptions = new PlugDescription[5];

    protected void setUp() throws Exception {
        this.fBus = new Bus(new DummyProxyFactory());
        for (int i = 0; i < this.plugDescriptions.length; i++) {
            this.plugDescriptions[i] = new PlugDescription();
            this.plugDescriptions[i].setProxyServiceURL("plug:" + i);
            this.plugDescriptions[i].addProvider(ORGANISATION);
            this.fBus.getIPlugRegistry().addPlugDescription(this.plugDescriptions[i]);
            this.fBus.getIPlugRegistry().activatePlug("plug:" + i);
        }
    }

    /**
     * @throws Exception
     */
    public void  testGroupingForDatasource() throws Exception {
	IngridQuery ingridQuery = QueryStringParser.parse("aQuery grouped:" + IngridQuery.GROUPED_BY_DATASOURCE);
	int hitsPerPage = 10;
	int maxMilliseconds = 1000;
	
	IngridHits hits = this.fBus.search(ingridQuery, hitsPerPage, 0, 0, maxMilliseconds);
	
	assertEquals(5, hits.getHits().length);
	IngridHit[] hits2 = hits.getHits();
	for (int i = 0; i< hits2.length; i++) {
		// was 2 now 1 ??????????? with changes svn Rev 17741
//	    assertEquals(2, hits2[i].getGroupTotalHitLength());
	    assertEquals(1, hits2[i].getGroupTotalHitLength());
	}
    }

    /**
     * @throws Exception
     */
    public void testGrouping() throws Exception {
        IngridQuery query = QueryStringParser.parse("aQuery grouped:" + IngridQuery.GROUPED_BY_ORGANISATION);
        int hitsPerPage = 10;
        int maxMilliseconds = 1000;
        IngridHits hits = this.fBus.search(query, hitsPerPage, 0, 0, maxMilliseconds);
        assertEquals(10, hits.length());
        assertEquals(1, hits.getHits().length);

        query = QueryStringParser.parse("aQuery grouped:" + IngridQuery.GROUPED_BY_PLUGID);
        hits = this.fBus.search(query, hitsPerPage, 0, 0, maxMilliseconds);
        assertEquals(10, hits.length());
        assertEquals(5, hits.getHits().length);
    }
    
    /**
     * @throws Exception
     */
    public void testGroupingINGRID_911() throws Exception {
        PlugDescription plugDesc = new PlugDescription();
        plugDesc.setProxyServiceURL("additional plug");
        plugDesc.addProvider("other organisation");
        this.fBus.getIPlugRegistry().addPlugDescription(plugDesc);
        this.fBus.getIPlugRegistry().activatePlug("additional plug");

        IngridQuery query = QueryStringParser.parse("aQuery grouped:" + IngridQuery.GROUPED_BY_ORGANISATION);
        int hitsPerPage = 10;
        int maxMilliseconds = 1000;
        IngridHits hits = this.fBus.search(query, hitsPerPage, 0, 0, maxMilliseconds);
        assertEquals(12, hits.length());
        assertEquals(2, hits.getHits().length);

        query = QueryStringParser.parse("aQuery grouped:" + IngridQuery.GROUPED_BY_PLUGID);
        hits = this.fBus.search(query, hitsPerPage, 0, 0, maxMilliseconds);
        assertEquals(12, hits.length());
        assertEquals(6, hits.getHits().length);
    }

    /**
     * @throws Exception
     */
    public void testHitPerPage() throws Exception {
        IngridQuery query = QueryStringParser.parse("aQuery grouped:" + IngridQuery.GROUPED_BY_PLUGID);
        int hitsPerPage = 10;
        int maxMilliseconds = 1000;
        IngridHits hits = this.fBus.search(query, hitsPerPage, 0, 0, maxMilliseconds);
        assertEquals(this.plugDescriptions.length * 2, hits.length());
        assertEquals(this.plugDescriptions.length, hits.getHits().length);

        // rising hits per page
        for (int i = 1; i < this.plugDescriptions.length; i++) {
            hits = this.fBus.search(query, i, 0, 0, maxMilliseconds);
            assertEquals(i, hits.getHits().length);
            assertEquals(this.plugDescriptions.length * 2, hits.length());
        }

        // rising start hit
        for (int i = 1; i < this.plugDescriptions.length; i++) {
            hits = this.fBus.search(query, hitsPerPage, 0, i, maxMilliseconds);
            switch (i) {
            case 1:
                assertEquals(5, hits.getHits().length);    
                break;
            case 2:
                assertEquals(4, hits.getHits().length);    
                break;
            case 3:
                assertEquals(4, hits.getHits().length);    
                break;
            case 4:
                assertEquals(3, hits.getHits().length);    
                break;
            default:
                fail();
                break;
            }
            
            assertEquals(this.plugDescriptions.length * 2, hits.length());
        }

        // 2 organisations
        PlugDescription plugDesc = new PlugDescription();
        plugDesc.setProxyServiceURL("0:0");
        plugDesc.addProvider("other organisation");
        this.fBus.getIPlugRegistry().addPlugDescription(plugDesc);
        this.fBus.getIPlugRegistry().activatePlug("0:0");
        query = QueryStringParser.parse("aQuery grouped:" + IngridQuery.GROUPED_BY_ORGANISATION);
        hits = this.fBus.search(query, hitsPerPage, 0, 0, maxMilliseconds);
        assertEquals(2, hits.getHits().length);
        assertEquals(12, hits.length());

        // 2 organisations - rising hits per page
        hits = this.fBus.search(query, 1, 0, 0, maxMilliseconds);
        assertEquals(1, hits.getHits().length);
        assertEquals(hits.getGoupedHitsLength(), countHits(hits.getHits(), 0));
        hits = this.fBus.search(query, 2, 0, 0, maxMilliseconds);
        assertEquals(2, hits.getHits().length);
        assertEquals((this.plugDescriptions.length + 1) * 2, hits.getGoupedHitsLength());

        // 2 organisations - rising start page
        hits = this.fBus.search(query, 1, 0, 0, maxMilliseconds);
        assertEquals(1, hits.getHits().length);
        int verbrateneHits = countHits(hits.getHits(), 0);
        assertFalse(hits.getHits()[0].getPlugId().equals(plugDesc.getPlugId()));
        hits = this.fBus.search(query, hitsPerPage, 0, hits.getGoupedHitsLength(), maxMilliseconds);
        assertEquals((this.plugDescriptions.length + 1) * 2, verbrateneHits + countHits(hits.getHits(), 0));

    }

    private int countHits(IngridHit[] hits, int currentHits) {

        for (int i = 0; i < hits.length; i++) {
            //System.out.println(hits.length + ".." + hits[i].getGroupHits().length);
            currentHits++;
            currentHits = countHits(hits[i].getGroupHits(), currentHits);
        }

        return currentHits;
    }

    /**
     * @throws Exception
     */
    public void testPaging() throws Exception {
        // setup
        Bus bus = new Bus(new DummyProxyFactory());
        int plugCount = 0;
        for (int i = 0; i < 25; i++) {
            // 50 different organisations
            for (int j = 0; j < 50; j++) {
                PlugDescription plugDescription = new PlugDescription();
                plugDescription.setProxyServiceURL(i + "_" + j);
                plugDescription.addProvider("" + i);
                bus.getIPlugRegistry().addPlugDescription(plugDescription);
                bus.getIPlugRegistry().activatePlug(i + "_" + j);
                plugCount++;
            }
        }

        IngridQuery query = QueryStringParser.parse("aQuery grouped:" + IngridQuery.GROUPED_BY_ORGANISATION);
        int hitsPerPage = 10;
        int maxMilliseconds = 10000;

        int foundHits = 0;
        long time = System.currentTimeMillis();
        while (foundHits < plugCount) {
            IngridHits hits = bus.search(query, hitsPerPage, 0, foundHits, maxMilliseconds);
            assertEquals(plugCount * 2, hits.length());
            foundHits = hits.getGoupedHitsLength();
            if (foundHits < plugCount) {
                assertEquals(hitsPerPage, hits.getHits().length);
            } else {
                assertEquals(10, hits.getHits().length);
            }
        }
        System.out.println("grouping took " + (System.currentTimeMillis() - time) + "ms");
    }
}
