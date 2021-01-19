/*
 * **************************************************-
 * InGrid iBus
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
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
package de.ingrid.ibus.comm;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.ibus.comm.registry.Registry;
import de.ingrid.utils.IPlug;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.query.IngridQuery;

public class Grouper implements IGrouper {

    private static Log LOG = LogFactory.getLog(Grouper.class);

    private final Registry _registry;

    public Grouper(Registry registry) {
        _registry = registry;
    }

    public IngridHits groupHits(IngridQuery query, IngridHit[] hits, int hitsPerPage, int totalHits, int startHit) throws Exception{
    	return groupHits(query, hits, hitsPerPage, totalHits, startHit, null);
	}
    
    public IngridHits groupHits(IngridQuery query, IngridHit[] hits, int hitsPerPage, int totalHits, int startHit, ResultSet resultSet)
            throws Exception {
        List groupHits = new ArrayList(hitsPerPage);
        int groupedHitsLength = 0;
        boolean newGroup;
        for (int i = 0; i < hits.length; i++) {
            IngridHit hit = hits[i];
            addGroupingInformation(hit, query);
            newGroup = true;
            if (IngridQuery.GROUPED_BY_DATASOURCE.equalsIgnoreCase(query.getGrouped())) {
                if (i > 0) {
                    if (areInSameGroup(hits[i - 1], hit)) {
                        IngridHit group = (IngridHit) groupHits.get(groupHits.size() - 1);
                        group.addGroupHit(hit);
                        newGroup = false;
                    }
                }
            } else {
                int size = groupHits.size();
                for (int j = 0; j < size; j++) {
                    IngridHit group = (IngridHit) groupHits.get(j);
                    if (areInSameGroup(hit, group)) {
                        group.addGroupHit(hit);
                        newGroup = false;
                    }
                }
            }
            if (newGroup) {
                if (groupHits.size() < hitsPerPage) {
                	if(resultSet != null){
                		addGroupHitsLengthForIPlug(resultSet, hit);
                	}
                	groupHits.add(hit); // we add the hit as new group
                } else {
                    break;
                }
            }
            groupedHitsLength++;
        }

        IngridHit[] groupedHits = (IngridHit[]) groupHits.toArray(new IngridHit[groupHits.size()]);

        groupHits.clear();
        groupHits = null;

        if (LOG.isDebugEnabled()) {
            LOG.debug("totalHits: " + totalHits + " groupedHits.lengh: " + groupedHits.length + " processedHits: "
                    + (groupedHitsLength + startHit));
        }
        return new IngridHits(totalHits, groupedHits, groupedHitsLength + startHit);
    }

    private void addGroupHitsLengthForIPlug(ResultSet resultSet, IngridHit hit) {
		String hitPlugId = hit.getPlugId();
		for(int i = 0; i<resultSet.size(); i++){
			IngridHits hits = (IngridHits) resultSet.get(i);
			if(hits.getPlugId() != null){
				if(hits.getPlugId().equals(hitPlugId)){
					hit.setGroupTotalHitLength((int)hits.length());
				}
			}
		}
    }

	private void addGroupingInformation(IngridHit hit, IngridQuery query) throws Exception {
        if (hit.getGroupedFields() != null) {
            return;
        }
        // XXX we just group for the 1st provider/partner
        if (IngridQuery.GROUPED_BY_PLUGID.equalsIgnoreCase(query.getGrouped())) {
            hit.addGroupedField(hit.getPlugId());
        } else if (IngridQuery.GROUPED_BY_PARTNER.equalsIgnoreCase(query.getGrouped())) {
            IPlug plug = _registry.getPlugProxy(hit.getPlugId());
            IngridHitDetail detail = plug.getDetail(hit, query, new String[] { PlugDescription.PARTNER });
            String[] partners = (String[]) detail.getArray(PlugDescription.PARTNER);
            for (int i = 0; partners != null && i < partners.length; i++) {
                hit.addGroupedField(partners[i]);
                break;
            }
        } else if (IngridQuery.GROUPED_BY_ORGANISATION.equalsIgnoreCase(query.getGrouped())) {
            IPlug plug = _registry.getPlugProxy(hit.getPlugId());
            IngridHitDetail detail = plug.getDetail(hit, query, new String[] { PlugDescription.PROVIDER });
            String[] providers = (String[]) detail.getArray(PlugDescription.PROVIDER);
            for (int i = 0; providers != null && i < providers.length; i++) {
                hit.addGroupedField(providers[i]);
                break;
            }
        } else if (IngridQuery.GROUPED_BY_DATASOURCE.equalsIgnoreCase(query.getGrouped())) {
            hit.addGroupedField(hit.getPlugId());
        } else {
            throw new IllegalArgumentException("unknown group operator '" + query.getGrouped() + '\'');
        }
        if (hit.getGroupedFields() == null || hit.getGroupedFields().length == 0) {
            hit.addGroupedField("no-detail-information:" + hit.getPlugId() + " (" + query.getGrouped() + ')');
            if (LOG.isWarnEnabled()) {
                LOG.warn("no-detail-information:" + hit.getPlugId() + " (" + query.getGrouped() + ')');
            }
        }
    }

    private boolean areInSameGroup(IngridHit group, IngridHit hit) {
        String[] groupFields = group.getGroupedFields();
        String[] hitFields = hit.getGroupedFields();
        for (int i = 0; i < groupFields.length; i++) {
            for (int j = 0; j < hitFields.length; j++) {
                if (groupFields[i].equalsIgnoreCase(hitFields[j])) {
                    return true;
                }
            }
        }
        return false;
    }

}
