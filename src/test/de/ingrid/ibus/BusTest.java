/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import junit.framework.TestCase;
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
public class BusTest extends TestCase {

    private static final String ORGANISATION = "a organisation";

    private Bus bus;

    private PlugDescription[] plugDescriptions = new PlugDescription[30];

    protected void setUp() throws Exception {
        this.bus = new Bus(new DummyProxyFactory());
        for (int i = 0; i < this.plugDescriptions.length; i++) {
            this.plugDescriptions[i] = new PlugDescription();
            this.plugDescriptions[i].setProxyServiceURL("" + i);
            this.plugDescriptions[i].setOrganisation(ORGANISATION);
            this.plugDescriptions[i].addField("ort");
            this.bus.getIPlugRegistry().addPlugDescription(this.plugDescriptions[i]);
        }
    }

    /**
     * @throws Exception
     */
    public void testSearch() throws Exception {
        IngridQuery query = QueryStringParser.parse("fische ort:halle ranking:score");

        for (int i = 0; i < 5; i++) {
            long time = System.currentTimeMillis();
            IngridHits hits = this.bus.search(query, this.plugDescriptions.length, 1, Integer.MAX_VALUE, 1000);
            System.out.println("search took " + (System.currentTimeMillis() - time) + " ms");
            assertEquals(this.plugDescriptions.length, hits.getHits().length);
            assertEquals(this.plugDescriptions.length, hits.getInVolvedPlugs());
            assertTrue(hits.isRanked());
        }

    }

    /**
     * @throws Exception
     */
    public void testFieldSearch() throws Exception {
        this.plugDescriptions[this.plugDescriptions.length - 1].addField("aField");
        IngridQuery query = QueryStringParser.parse("aField:halle");
        IngridHits hits = this.bus.search(query, 10, 1, Integer.MAX_VALUE, 1000);
        assertEquals(1, hits.getHits().length);
        assertEquals(1, hits.getInVolvedPlugs());
    }

    /**
     * @throws Exception
     */
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
    public void testUnrankedSearch() throws Exception {
        this.bus = new Bus(new DummyProxyFactory());
        this.plugDescriptions = new PlugDescription[3];
        for (int i = 0; i < this.plugDescriptions.length; i++) {
            this.plugDescriptions[i] = new PlugDescription();
            this.plugDescriptions[i].setProxyServiceURL("" + i);
            this.plugDescriptions[i].setOrganisation(ORGANISATION);
            this.plugDescriptions[i].addField("ort");
            this.bus.addPlugDescription(this.plugDescriptions[i]);
        }

        IngridQuery query = QueryStringParser.parse("fische");
        query.put(IngridQuery.RANKED, IngridQuery.NOT_RANKED);
        IngridHits hits = this.bus.search(query, 10, 1, Integer.MAX_VALUE, 1000);
        assertEquals(this.plugDescriptions.length, hits.getHits().length);
        assertEquals(this.plugDescriptions.length, hits.getInVolvedPlugs());
        assertFalse(hits.isRanked());
        // for (int i = 0; i < this.plugDescriptions.length; i++) {
        // assertEquals(hits.getHits()[i].getPlugId(),
        // this.plugDescriptions[i].getPlugId());
        // }

        // invert order of plugdescriptions
        this.bus.removePlugDescription(this.plugDescriptions[0]);
        this.bus.removePlugDescription(this.plugDescriptions[1]);
        assertEquals(1, this.bus.search(query, 10, 1, Integer.MAX_VALUE, 1000).length());
        this.bus.addPlugDescription(this.plugDescriptions[1]);
        this.bus.addPlugDescription(this.plugDescriptions[0]);

        hits = this.bus.search(query, 10, 1, Integer.MAX_VALUE, 1000);
        // for (int i = 0; i < this.plugDescriptions.length; i++) {
        // assertEquals(hits.getHits()[i].getPlugId(),
        // this.plugDescriptions[this.plugDescriptions.length - i - 1]
        // .getPlugId());
        // }

    }

    /**
     * @throws Exception
     */
    public void testUnrankedGroupedDatatypeSearch() throws Exception {
        this.bus = new Bus(new DummyProxyFactory());
        this.plugDescriptions = new PlugDescription[3];
        for (int i = 0; i < this.plugDescriptions.length; i++) {
            this.plugDescriptions[i] = new PlugDescription();
            this.plugDescriptions[i].setProxyServiceURL("" + i);
            this.plugDescriptions[i].setOrganisation(ORGANISATION);
            this.plugDescriptions[i].addDataType("g2k");
            this.bus.addPlugDescription(this.plugDescriptions[i]);
        }

        IngridQuery query = QueryStringParser.parse("fische ranking:off datatype:g2k grouped:grouped_by_organisation");
        IngridHits hits = this.bus.search(query, 10, 1, 0, 1000);
        assertEquals(this.plugDescriptions.length, hits.getHits().length);
    }

    /**
     * Test query with partners in sub clauses.
     * @throws Exception
     */
    public void testFilterForPartner() throws Exception {
        this.bus = new Bus(new DummyProxyFactory());
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
}
