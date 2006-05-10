package de.ingrid.ibus;

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
            this.fBus.getIPlugRegistry().addIPlug(this.plugDescriptions[i]);
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
        assertEquals(5, hits.length());
        assertEquals(1, hits.getHits().length);

        query = QueryStringParser.parse("aQuery grouped:" + IngridQuery.GROUPED_BY_PLUGID);
        hits = this.fBus.search(query, hitsPerPage, 0, 0, maxMilliseconds);
        assertEquals(5, hits.length());
        assertEquals(5, hits.getHits().length);
    }

    /**
     * @throws Exception
     */
    public void testGroupingINGRID_911() throws Exception {
        PlugDescription plugDesc = new PlugDescription();
        plugDesc.setProxyServiceURL("additional plug");
        plugDesc.addProvider("other organisation");
        this.fBus.getIPlugRegistry().addIPlug(plugDesc);

        IngridQuery query = QueryStringParser.parse("aQuery grouped:" + IngridQuery.GROUPED_BY_ORGANISATION);
        int hitsPerPage = 10;
        int maxMilliseconds = 1000;
        IngridHits hits = this.fBus.search(query, hitsPerPage, 0, 0, maxMilliseconds);
        assertEquals(6, hits.length());
        assertEquals(2, hits.getHits().length);

        query = QueryStringParser.parse("aQuery grouped:" + IngridQuery.GROUPED_BY_PLUGID);
        hits = this.fBus.search(query, hitsPerPage, 0, 0, maxMilliseconds);
        assertEquals(6, hits.length());
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
        assertEquals(this.plugDescriptions.length, hits.length());
        assertEquals(this.plugDescriptions.length, hits.getHits().length);

        // rising hits per page
        for (int i = 1; i < this.plugDescriptions.length; i++) {
            hits = this.fBus.search(query, i, 0, 0, maxMilliseconds);
            assertEquals(i, hits.getHits().length);
            assertEquals(this.plugDescriptions.length, hits.length());
        }

        // rising start hit
        for (int i = 1; i < this.plugDescriptions.length; i++) {
            hits = this.fBus.search(query, hitsPerPage, 0, i, maxMilliseconds);
            assertEquals(this.plugDescriptions.length - i, hits.getHits().length);
            assertEquals(this.plugDescriptions.length, hits.length());
        }

        // 2 organisations
        PlugDescription plugDesc = new PlugDescription();
        plugDesc.setProxyServiceURL("additional plug");
        plugDesc.addProvider("other organisation");
        this.fBus.getIPlugRegistry().addIPlug(plugDesc);
        query = QueryStringParser.parse("aQuery grouped:" + IngridQuery.GROUPED_BY_ORGANISATION);
        hits = this.fBus.search(query, hitsPerPage, 0, 0, maxMilliseconds);
        assertEquals(2, hits.getHits().length);
        assertEquals(this.plugDescriptions.length + 1, hits.length());

        // 2 organisations - rising hits per page
        hits = this.fBus.search(query, 1, 0, 0, maxMilliseconds);
        assertEquals(1, hits.getHits().length);
        hits = this.fBus.search(query, 2, 0, 0, maxMilliseconds);
        assertEquals(2, hits.getHits().length);

        // 2 organisations - rising start page
        hits = this.fBus.search(query, 1, 0, 0, maxMilliseconds);
        assertEquals(1, hits.getHits().length);
        assertFalse(hits.getHits()[0].getPlugId().equals(plugDesc.getPlugId()));
        hits = this.fBus.search(query, hitsPerPage, 0, hits.getGoupedHitsLength(), maxMilliseconds);
        assertEquals(1, hits.getHits().length);
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
                plugDescription.setProxyServiceURL("" + i + "_" + j);
                plugDescription.addProvider("" + i);
                bus.getIPlugRegistry().addIPlug(plugDescription);
                plugCount++;
            }
        }

        IngridQuery query = QueryStringParser.parse("aQuery grouped:" + IngridQuery.GROUPED_BY_ORGANISATION);
        int hitsPerPage = 10;
        int maxMilliseconds = 1000;

        int foundHits = 0;
        long time = System.currentTimeMillis();
        while (foundHits < plugCount) {
            IngridHits hits = bus.search(query, hitsPerPage, 0, foundHits, maxMilliseconds);
            assertEquals(plugCount, hits.length());
            foundHits = hits.getGoupedHitsLength();
            if (foundHits < plugCount) {
                assertEquals(hitsPerPage, hits.getHits().length);
            } else {
                assertEquals(5, hits.getHits().length);
            }
        }
        System.out.println("grouping took " + (System.currentTimeMillis() - time));
    }

    // /**
    // * // XXX we just group for the 1st provider/partner
    // * @throws Exception
    // */
    // public void testHitGroupedToTwoGroups() throws Exception {
    // PlugDescription plugDesc = new PlugDescription();
    // plugDesc.setProxyServiceURL("additional plug");
    // plugDesc.addProvider(this.plugDescriptions[0].getProviders()[0]);
    // plugDesc.addProvider("additional organisation");
    // this.fBus.getIPlugRegistry().addIPlug(plugDesc);
    //        
    // IngridQuery query = QueryStringParser.parse("aQuery grouped:" +
    // IngridQuery.GROUPED_BY_ORGANISATION);
    // IngridHits hits = this.fBus.search(query, 10, 0, 0, 1000);
    // assertEquals(2, hits.getHits().length);
    // assertEquals(this.plugDescriptions.length + 1, hits.length());
    // }

}
