package de.ingrid.ibus;

import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;
import junit.framework.TestCase;

/**
 * TODO comment for GroupingTest 
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
            this.plugDescriptions[i].setProxyServiceURL("" + i);
            this.plugDescriptions[i].setOrganisation(ORGANISATION);
            this.fBus.getIPlugRegistry().addIPlug(this.plugDescriptions[i]);
        }
    }

    /**
     * @throws Exception
     */
    public void testGrouping() throws Exception {

        IngridQuery query = QueryStringParser.parse("aQuery grouped:" + IngridQuery.GROUPED_BY_ORGANISATION);
        int hitsPerPage = 10;
        int currentPage = 1;
        int length = 1000;
        int maxMilliseconds = 1000;
        IngridHits hits = this.fBus.search(query, hitsPerPage, currentPage, length, maxMilliseconds);
        assertEquals(5, hits.length());
        assertEquals(1, hits.getHits().length);

        query = QueryStringParser.parse("aQuery grouped:" + IngridQuery.GROUPED_BY_PLUGID);
        hits = this.fBus.search(query, hitsPerPage, currentPage, length, maxMilliseconds);
        assertEquals(5, hits.length());
        assertEquals(5, hits.getHits().length);

    }
    
    /**
     * @throws Exception
     */
    public void testGroupingINGRID_911() throws Exception {
       PlugDescription plugDesc = new PlugDescription();
        plugDesc.setProxyServiceURL("additional plug" );
        plugDesc.setOrganisation("other organisation");
        this.fBus.getIPlugRegistry().addIPlug(plugDesc);
        
        IngridQuery query = QueryStringParser.parse("aQuery grouped:" + IngridQuery.GROUPED_BY_ORGANISATION);
        int hitsPerPage = 10;
        int currentPage = 1;
        int length = 1000;
        int maxMilliseconds = 1000;
        IngridHits hits = this.fBus.search(query, hitsPerPage, currentPage, length, maxMilliseconds);
        assertEquals(6, hits.length());
        assertEquals(2, hits.getHits().length);

        query = QueryStringParser.parse("aQuery grouped:" + IngridQuery.GROUPED_BY_PLUGID);
        hits = this.fBus.search(query, hitsPerPage, currentPage, length, maxMilliseconds);
        assertEquals(6, hits.length());
        assertEquals(6, hits.getHits().length);
    }

    /**
     * @throws Exception
     */
    public void testPaging() throws Exception {
        // setup
        Bus bus = new Bus(new DummyProxyFactory());
        int count = 0;
        for (int i = 0; i < 25; i++) {
            for (int j = 0; j < 50; j++) {
                PlugDescription plugDescription = new PlugDescription();
                plugDescription.setProxyServiceURL("" + i + "_" + j);
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
        long tmp1 = hits.length();  // 1.250
        int tmp2 = hits.getHits().length;  // 10
        assertEquals(count, hits.length());
        assertEquals(10, hits.getHits().length);
        
        currentPage = 2;
        hits = bus.search(query, hitsPerPage, currentPage, length, maxMilliseconds);
        tmp1 = hits.length(); // 1.250
        tmp2 = hits.getHits().length; // 10
        assertEquals(count, hits.length());
        assertEquals(10, hits.getHits().length);
        
        currentPage = 3;
        hits = bus.search(query, hitsPerPage, currentPage, length, maxMilliseconds);
        tmp1 = hits.length();  // 1.250
        tmp2 = hits.getHits().length; // 5
        assertEquals(count, hits.length());
        assertEquals(5, hits.getHits().length);
        
        currentPage = 4;
        // SCHMEISST ArrayIndexOutOfBoundsException !!!!!
        hits = bus.search(query, hitsPerPage, currentPage, length, maxMilliseconds);
        assertEquals(count, hits.length());
        assertEquals(5, hits.getHits().length);

    }

}
