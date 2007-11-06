package de.ingrid.ibus;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.ibus.registry.Registry;
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

    public IngridHits groupHits(IngridQuery query, IngridHit[] hits, int hitsPerPage, int startHit) throws Exception {
        // list for collecting groups
        List groupHitList = new ArrayList(hitsPerPage);
        int groupedHitsLength = startHit;
        boolean newGroup;
        int groupCount = 0;
        // loop over every hit
        for (int i = 0; i < hits.length; i++) {
            IngridHit hit = hits[i];
            addGroupingInformation(hit, query);
            newGroup = true;
            int size = groupHitList.size();
            for (int j = 0; j < size; j++) {
                IngridHit group = (IngridHit) groupHitList.get(j);
                if (areInSameGroup(hit, group)) {
                    group.addGroupHit(hit);
                    newGroup = false;
                }
            }
            if (newGroup) {
                // if new -> add to list
                groupHitList.add(hit); // we add the hit as new group
                // increase group count
                groupCount++;
            }
            // we collect only hits greater than the startHit, because we search
            // from 0
            if (newGroup && groupCount <= hitsPerPage) {
                groupedHitsLength++;
            }

        }

        IngridHit[] groupedHits = (IngridHit[]) groupHitList.toArray(new IngridHit[groupHitList.size()]);
        groupHitList.clear();
        groupHitList = null;

        if (LOG.isDebugEnabled()) {
            LOG.debug("hits.length:" + hits.length + " groupCount: " + groupCount + " groupedHits.length: "
                    + groupedHits.length + " groupedHitsLength: " + groupedHitsLength);
        }
        return new IngridHits(groupCount, groupedHits, groupedHitsLength);
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
