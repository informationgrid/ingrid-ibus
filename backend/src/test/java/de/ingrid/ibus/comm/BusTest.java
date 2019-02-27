/*
 * **************************************************-
 * InGrid iBus
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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

package de.ingrid.ibus.comm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import de.ingrid.ibus.comm.registry.Registry;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.processor.impl.StatisticPostProcessor;
import de.ingrid.utils.processor.impl.StatisticPreProcessor;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * BusTest
 * 
 * <p/>created on 07.09.2005
 * 
 * @version $Revision: $
 * @author sg
 * @author $Author jz ${lastedit}
 * 
 */
public class BusTest {

    private static final String ORGANISATION = "a organisation";

    private Bus bus;

    private PlugDescription[] plugDescriptions = new PlugDescription[30];

    public BusTest() throws Exception {
        //setUp();
    }

    @Before
    public void setUp() throws Exception {
        this.bus = new Bus(new DummyProxyFactory(), null);
        Registry registry = this.bus.getIPlugRegistry();
        for (int i = 0; i < this.plugDescriptions.length; i++) {
            this.plugDescriptions[i] = new PlugDescription();
            this.plugDescriptions[i].setProxyServiceURL("" + i);
            this.plugDescriptions[i].setOrganisation(ORGANISATION);
            this.plugDescriptions[i].addField("ort");
            this.bus.getIPlugRegistry().addPlugDescription(this.plugDescriptions[i]);
            registry.activatePlug("" + i);
        }
    }

    /**
     * @throws Exception
     */
    @Test
    public void testSearch() throws Exception {
        IngridQuery query = QueryStringParser.parse("fische ort:halle ranking:score");

        for (int i = 0; i < 5; i++) {
            long time = System.currentTimeMillis();
            IngridHits hits = this.bus.search(query, this.plugDescriptions.length, 1, Integer.MAX_VALUE, 1000);
            System.out.println("search took ".concat(new Long(System.currentTimeMillis() - time).toString().concat(" ms")));
            assertEquals(this.plugDescriptions.length, hits.getHits().length);
            assertEquals(this.plugDescriptions.length, hits.getInVolvedPlugs());
            assertTrue(hits.isRanked());
        }

    }

    /**
     * @throws Exception
     */
    @Test
    public void testFieldSearch() throws Exception {
        this.plugDescriptions[this.plugDescriptions.length - 1].addField("aField");
        IngridQuery query = QueryStringParser.parse("aField:halle");
        IngridHits hits = this.bus.search(query, 10, 1, Integer.MAX_VALUE, 1000);
        assertEquals(2, hits.getHits().length);
        assertEquals(1, hits.getInVolvedPlugs());
    }

    /**
     * @throws Exception
     */
    @Test
    public void testSearchWithStatisticProcessors() throws Exception {
        // TODO: make this test log implementation independent
        this.bus.getProccessorPipe().addPreProcessor(new StatisticPreProcessor());
        this.bus.getProccessorPipe().addPostProcessor(new StatisticPostProcessor());

        IngridQuery query = QueryStringParser.parse("fische ort:halle");
        this.bus.search(query, 10, 1, Integer.MAX_VALUE, 1000);
    }

    /**
     * Test the instanciation process.
     */
    @Test
    public void testAddRemoveIPlug() {
        assertEquals(this.plugDescriptions.length, this.bus.getIPlugRegistry().getAllIPlugs().length);
        PlugDescription pd = new PlugDescription();
        pd.setProxyServiceURL("bla");
        this.bus.addPlugDescription(pd);
        assertEquals(this.plugDescriptions.length + 1, this.bus.getIPlugRegistry().getAllIPlugs().length);
        this.bus.removePlugDescription(pd);
        assertEquals(this.plugDescriptions.length, this.bus.getIPlugRegistry().getAllIPlugs().length);
        assertNull(this.bus.getIPlugRegistry().getPlugProxy(pd.getPlugId()));
    }

    /**
     * @throws Exception
     */
    @Test
    public void testGetHitDetail() throws Exception {

        IngridQuery query = QueryStringParser.parse("fische ort:halle");
        IngridHits hits = this.bus.search(query, this.plugDescriptions.length, 1, Integer.MAX_VALUE, 1000);
        assertEquals(this.plugDescriptions.length, hits.getHits().length);
        IngridHit[] hitArray = hits.getHits();
        for (int i = 0; i < hitArray.length; i++) {
            IngridHit hit = hitArray[i];
            IngridHitDetail details = this.bus.getDetail(hit, query, new String[0]);
            assertNotNull(details);
            assertEquals(DummyIPlug.TITLE, details.getTitle());

            String plugId = details.getPlugId();
            PlugDescription plugDescription = this.bus.getIPlugRegistry().getPlugDescription(plugId);
            assertEquals(ORGANISATION, plugDescription.getOrganisation());
        }

    }

    /**
     * @throws Exception
     */
    @Test
    public void testGetHitDetails() throws Exception {
        IngridQuery query = QueryStringParser.parse("fische ort:halle");
        IngridHits hits = this.bus.search(query, this.plugDescriptions.length, 1, Integer.MAX_VALUE, 1000);

        assertEquals(this.plugDescriptions.length, hits.getHits().length);
        IngridHit[] hitArray = hits.getHits();
        IngridHitDetail[] details = this.bus.getDetails(hitArray, query, new String[0]);

        for (int i = 0; i < details.length; i++) {
            IngridHitDetail detail = details[i];
            assertEquals(detail.getDocumentId(), hitArray[i].getDocumentId());
        }
    }

    /**
     * @throws Exception
     */
    @Test
    public void testUnrankedSearch() throws Exception {
        this.bus = new Bus(new DummyProxyFactory(), null);
        Registry registry = this.bus.getIPlugRegistry();
        this.plugDescriptions = new PlugDescription[3];
        for (int i = 0; i < this.plugDescriptions.length; i++) {
            this.plugDescriptions[i] = new PlugDescription();
            this.plugDescriptions[i].setProxyServiceURL("" + i);
            this.plugDescriptions[i].setOrganisation(ORGANISATION);
            this.plugDescriptions[i].addField("ort");
            this.bus.addPlugDescription(this.plugDescriptions[i]);
            registry.activatePlug("" + i);
        }

        IngridQuery query = QueryStringParser.parse("fische");
        query.put(IngridQuery.RANKED, IngridQuery.NOT_RANKED);
        IngridHits hits = this.bus.search(query, 10, 1, Integer.MAX_VALUE, 1000);
        assertEquals((this.plugDescriptions.length) * 2, hits.getHits().length);
        assertEquals(this.plugDescriptions.length, hits.getInVolvedPlugs());
        assertFalse(hits.isRanked());

        // invert order of plugdescriptions
        this.bus.removePlugDescription(this.plugDescriptions[0]);
        this.bus.removePlugDescription(this.plugDescriptions[1]);
        assertEquals(2, this.bus.search(query, 10, 1, Integer.MAX_VALUE, 1000).length());
        this.bus.addPlugDescription(this.plugDescriptions[1]);
        this.bus.addPlugDescription(this.plugDescriptions[0]);

        hits = this.bus.search(query, 10, 1, Integer.MAX_VALUE, 1000);
        assertFalse(hits.isRanked());
    }

    /**
     * @throws Exception
     */
    @Test
    public void testUnrankedGroupedDatatypeSearch() throws Exception {
        this.bus = new Bus(new DummyProxyFactory(), null);
        Registry registry = this.bus.getIPlugRegistry();
        this.plugDescriptions = new PlugDescription[3];
        for (int i = 0; i < this.plugDescriptions.length; i++) {
            this.plugDescriptions[i] = new PlugDescription();
            this.plugDescriptions[i].setProxyServiceURL("" + i);
            this.plugDescriptions[i].setOrganisation(ORGANISATION);
            this.plugDescriptions[i].addDataType("g2k");
            this.bus.addPlugDescription(this.plugDescriptions[i]);
            registry.activatePlug("" + i);
        }

        IngridQuery query = QueryStringParser.parse("fische ranking:off datatype:g2k grouped:grouped_by_organisation");
        IngridHits hits = this.bus.search(query, 10, 1, 0, 1000);
        assertEquals(this.plugDescriptions.length, hits.getHits().length);
    }

    /**
     * Test query with partners in sub clauses.
     * @throws Exception
     */
    @Test
    public void testFilterForPartner() throws Exception {
        this.bus = new Bus(new DummyProxyFactory(), null);
        this.plugDescriptions = new PlugDescription[3];
        for (int i = 0; i < this.plugDescriptions.length; i++) {
            this.plugDescriptions[i] = new PlugDescription();
            this.plugDescriptions[i].setProxyServiceURL("" + i);
            this.plugDescriptions[i].setOrganisation(ORGANISATION);
            this.plugDescriptions[i].addDataType("g2k");
            this.plugDescriptions[i].addPartner("he");
            this.plugDescriptions[i].addProvider("he");
            this.bus.addPlugDescription(this.plugDescriptions[i]);
        }

        IngridQuery query = QueryStringParser.parse("fische (partner:st OR partner:sl)");
        IngridHits hits = this.bus.search(query, 10, 1, 0, 1000);
        assertEquals(0, hits.getHits().length);
    }

    /**
     * @throws Exception
     */
    @Test
    public void testGroupByPlugId() throws Exception {
        this.bus = new Bus(new DummyProxyFactory(), null);
        Registry registry = this.bus.getIPlugRegistry();
        this.plugDescriptions = new PlugDescription[3];
        for (int i = 0; i < this.plugDescriptions.length; i++) {
            this.plugDescriptions[i] = new PlugDescription();
            this.plugDescriptions[i].setProxyServiceURL("" + i);
            this.plugDescriptions[i].setOrganisation(ORGANISATION);
            this.plugDescriptions[i].addDataType("g2k");
            this.bus.addPlugDescription(this.plugDescriptions[i]);
            registry.activatePlug("" + i);
        }

        IngridQuery query = QueryStringParser.parse("fische ranking:off grouped:grouped_by_plugId");
        IngridHits hits = this.bus.search(query, 10, 1, 0, 1000);
        assertEquals(this.plugDescriptions.length, hits.getHits().length);
        System.out.println(hits);
        System.out.println(hits.getHits()[0].size());
    }
    
    /**
     * Comparison tests
     * With Java7 comparisons are more strict and can throw exceptions if
     * the comparator is not valid (reflexive, symmetrical and transitive)
     */
    @Test
    @SuppressWarnings("unchecked")
	public void testSortHitsSpecial() {
    	IngridHit[] documents = new IngridHit[32];
    	for (int i = 0; i < documents.length; i++) {
    		if (i%2 == 0) {
    			documents[i] = null;
    			continue;
    		}
			documents[i] = new IngridHit();
			documents[i].setScore(1.0f);
		}
    	documents[29].setScore(1.4f);
    	
        Arrays.sort(documents, Comparators.SCORE_HIT_COMPARATOR);
        
        assertEquals(1.4f, documents[0].getScore(), 0);
        for (int i = 1; i < documents.length/2; i++) {
        	assertEquals(1.0f, documents[i].getScore(), 0);
        }
        assertNull(documents[documents.length-1]);
    }
    
    @SuppressWarnings("unchecked")
    @Test
	public void testSortHits() {
    	IngridHit[] documents = new IngridHit[32];
        // *******************************
    	// 16 -> NULL, 15 -> 1.0, 1 -> 1.4
        // *******************************
    	for (int i = 0; i < documents.length; i++) {
    		if (i<documents.length/2) {
    			documents[i] = null;
    			continue;
    		}
			documents[i] = new IngridHit();
			documents[i].setScore(1.0f);
		}
    	documents[29].setScore(1.4f);
    	
        Arrays.sort(documents, Comparators.SCORE_HIT_COMPARATOR);
        checkSortedDocuments(documents);
        
        // *******************************
        // 15 -> 1.0, 1 -> 1.4, 16 -> NULL
        // *******************************
    	for (int i = 0; i < documents.length; i++) {
    		if (i>=documents.length/2) {
    			documents[i] = null;
    			continue;
    		}
			documents[i] = new IngridHit();
			documents[i].setScore(1.0f);
		}
    	documents[11].setScore(1.4f);
    	
        Arrays.sort(documents, Comparators.SCORE_HIT_COMPARATOR);
        checkSortedDocuments(documents);
        
        // *******************************
        // 15 -> 1.0, 16 -> NULL, 1 -> 1.4
        // *******************************
    	for (int i = 0; i < documents.length; i++) {
    		if (i>=documents.length/2) {
    			documents[i] = null;
    			continue;
    		}
			documents[i] = new IngridHit();
			documents[i].setScore(1.0f);
		}
    	documents[31] = new IngridHit();
    	documents[31].setScore(1.4f);
    	
        Arrays.sort(documents, Comparators.SCORE_HIT_COMPARATOR);
        checkSortedDocuments(documents);
        
        // *******************************
        // 1 -> 1.4, 15 -> 1.0, 16 -> NULL
        // *******************************
    	for (int i = 0; i < documents.length; i++) {
    		if (i>=documents.length/2) {
    			documents[i] = null;
    			continue;
    		}
			documents[i] = new IngridHit();
			documents[i].setScore(1.0f);
		}
    	documents[0].setScore(1.4f);
    	
        Arrays.sort(documents, Comparators.SCORE_HIT_COMPARATOR);
        checkSortedDocuments(documents);
    }
    
    private void checkSortedDocuments(IngridHit[] documents) {
    	assertEquals(1.4f, documents[0].getScore(), 0);
        for (int i = 1; i < documents.length/2; i++) {
        	assertEquals(1.0f, documents[i].getScore(), 0);
        }
        for (int i = documents.length/2; i < documents.length; i++) {
        	assertNull(documents[documents.length-1]);
        }
    }
}
