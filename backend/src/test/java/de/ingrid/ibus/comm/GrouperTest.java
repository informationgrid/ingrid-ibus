/*
 * **************************************************-
 * InGrid iBus
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.ibus.comm;

import de.ingrid.ibus.service.SettingsService;
import de.ingrid.ibus.comm.registry.Registry;

import org.junit.jupiter.api.Test;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;

public class GrouperTest {

    @Test
    public void testGrouping() throws Exception {
        IGrouper grouper = new Grouper(new Registry(10, true, new DummyProxyFactory(), new SettingsService()));
        IngridHit[] hits = new IngridHit[23];
        for (int i = 0; i < hits.length; i++) {
            hits[i] = new IngridHit();
            String group = "partner";
            if (i % 4 == 0) {
                group = "provider";
            }
            hits[i].addGroupedField(group);
            hits[i].setDocumentId(String.valueOf(i));
        }
        int startHit = 1;
        int hitsPerPage = 10;
        IngridQuery query = new IngridQuery(true, false, -1, "abc");
        query.put("grouped", IngridQuery.GROUPED_BY_DATASOURCE);
        IngridHits groupHits = grouper.groupHits(query, hits, hitsPerPage, 23, startHit);
        System.out.println(groupHits.getGoupedHitsLength());
        System.out.println(groupHits.getHits().length);
        System.out.println(groupHits.length());
    }
}
