package de.ingrid.ibus;

import junit.framework.TestCase;
import de.ingrid.ibus.registry.Registry;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHits;

public class GrouperTest extends TestCase {

    public void testGrouping() throws Exception {
        IGrouper grouper = new Grouper(new Registry(10, true, new DummyProxyFactory()));
        IngridHit[] hits = new IngridHit[23];
        for (int i = 0; i < hits.length; i++) {
            hits[i] = new IngridHit();
            String group = "partner";
            if (i % 4 == 0) {
                group = "provider";
            }
            hits[i].addGroupedField(group);
            hits[i].setDocumentId(i);
        }
        int startHit = 1;
        int hitsPerPage = 10;
        IngridHits groupHits = grouper.groupHits(null, hits, hitsPerPage, 23, startHit);
        System.out.println(groupHits.getGoupedHitsLength());
        System.out.println(groupHits.getHits().length);
        System.out.println(groupHits.length());
    }
}
