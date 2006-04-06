package de.ingrid.ibus;

import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;
import junit.framework.TestCase;

public class GroupingTest extends TestCase {

//    private static final String ORGANISATION = "a organisation";
//
//    private Bus fBus;
//
//    private PlugDescription[] plugDescriptions = new PlugDescription[5];
//
//    protected void setUp() throws Exception {
//
//        this.fBus = new Bus(new DummyProxyFactory());
//        for (int i = 0; i < this.plugDescriptions.length; i++) {
//            this.plugDescriptions[i] = new PlugDescription();
//            this.plugDescriptions[i].setPlugId("" + i);
//            this.plugDescriptions[i].setOrganisation(ORGANISATION);
//            this.fBus.getIPlugRegistry().addIPlug(this.plugDescriptions[i]);
//        }
//    }
//
//    public void testGrouping() throws Exception {
//
//        IngridQuery query = QueryStringParser.parse("aQuery grouped:" + IngridQuery.GROUPED_BY_ORGANISATION);
//        int hitsPerPage = 10;
//        int currentPage = 1;
//        int length = 1000;
//        int maxMilliseconds = 1000;
//        IngridHits hits = fBus.search(query, hitsPerPage, currentPage, length, maxMilliseconds);
//        assertEquals(5, hits.length());
//        assertEquals(1, hits.getHits().length);
//
//        query = QueryStringParser.parse("aQuery grouped:" + IngridQuery.GROUPED_BY_PLUGID);
//        hits = fBus.search(query, hitsPerPage, currentPage, length, maxMilliseconds);
//        assertEquals(5, hits.length());
//        assertEquals(5, hits.getHits().length);
//
//    }

    public void testPaging() throws Exception {
        // setup
        Bus bus = new Bus(new DummyProxyFactory());
        int count = 0;
        for (int i = 0; i < 25; i++) {
            for (int j = 0; j < 50; j++) {
                PlugDescription plugDescription = new PlugDescription();
                plugDescription.setPlugId("" + i + "_" + j);
                plugDescription.setOrganisation(""+i);
                bus.getIPlugRegistry().addIPlug(plugDescription);
                count ++;
            }
        }
        
        IngridQuery query = QueryStringParser.parse("aQuery grouped:" + IngridQuery.GROUPED_BY_ORGANISATION);
        int hitsPerPage = 10;
        int currentPage = 1;
        int length = 1000;
        int maxMilliseconds = 1000;
        IngridHits hits = bus.search(query, hitsPerPage, currentPage, length, maxMilliseconds);
        assertEquals(count, hits.length());
        assertEquals(10, hits.getHits().length);
        
        currentPage = 2;
        hits = bus.search(query, hitsPerPage, currentPage, length, maxMilliseconds);
        assertEquals(count, hits.length());
        assertEquals(10, hits.getHits().length);
        
        currentPage = 3;
        hits = bus.search(query, hitsPerPage, currentPage, length, maxMilliseconds);
        assertEquals(count, hits.length());
        assertEquals(5, hits.getHits().length);
        

    }

}
