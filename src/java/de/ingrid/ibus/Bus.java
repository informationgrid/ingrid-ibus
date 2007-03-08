/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.ibus.net.IPlugProxyFactory;
import de.ingrid.ibus.net.PlugQueryRequest;
import de.ingrid.ibus.registry.Registry;
import de.ingrid.ibus.registry.SyntaxInterpreter;
import de.ingrid.utils.IBus;
import de.ingrid.utils.IPlug;
import de.ingrid.utils.IRecordLoader;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.processor.ProcessorPipe;
import de.ingrid.utils.query.IngridQuery;

/**
 * The IBus a centralized Bus that routes queries and return results. Created on 09.08.2005
 * 
 * @author sg
 * @version $Revision: 1.3 $
 */
public class Bus extends Thread implements IBus {

    private static final long serialVersionUID = Bus.class.getName().hashCode();

    private static Log fLogger = LogFactory.getLog(Bus.class);

    private static Bus fInstance;
    
    // TODO INGRID-398 we need to made the lifetime configurable.
    private Registry fRegistry;

    private ProcessorPipe fProcessorPipe = new ProcessorPipe();

    /**
     * The bus. All IPlugs have to connect with the bus to be searched. It sends queries to regeistered and activated
     * iplugs. It only sends a query to a iplug if it is able to handle the query. For all implemented criteria see
     * de.ingrid.ibus.registry.SyntaxInterpreter#getIPlugsForQuery(IngridQuery, Registry) .
     * 
     * @param factory
     *            A factroy for creating iplug proxies.
     * @see de.ingrid.ibus.registry.SyntaxInterpreter#getIPlugsForQuery(IngridQuery, Registry)
     */
    public Bus(IPlugProxyFactory factory) {
        this.fRegistry = new Registry(100000, false, factory);
        fInstance = this;
    }

    /**
     * Do not use this method. Only for internal usage.
     * 
     * @return The bus instance, if it was initialised.
     * @deprecated
     */
    public static Bus getInstance() {
        return fInstance;
    }

    public IngridHits search(IngridQuery query, final int hitsPerPage, int currentPage, int startHit,
            int maxMilliseconds) throws Exception {
        if (fLogger.isDebugEnabled()) {
            fLogger.debug("search for: " + query.toString() + " startHit: " + startHit + " started");
        }
        if (currentPage < 1) {
            currentPage = 1;
        }
        this.fProcessorPipe.preProcess(query);
        boolean grouping = query.getGrouped() != null && !query.getGrouped().equalsIgnoreCase(IngridQuery.GROUPED_OFF);

        int requestLength;
        if (!grouping) {
            requestLength = hitsPerPage * currentPage;
        } else {
            requestLength = startHit + (hitsPerPage * 6);
        }
        PlugDescription[] plugDescriptionsForQuery = SyntaxInterpreter.getIPlugsForQuery(query, this.fRegistry);
        ResultSet resultSet = requestHits(query, maxMilliseconds, plugDescriptionsForQuery, 0, requestLength);

        IngridHits hitContainer;
        if (query.isNotRanked()) {
            hitContainer = orderResults(resultSet, plugDescriptionsForQuery);
        } else {
            hitContainer = normalizeScores(resultSet);
        }
        IngridHit[] hits = hitContainer.getHits();
        int totalHits = (int) hitContainer.length();
        if (hits.length > 0) {
            this.fProcessorPipe.postProcess(query, hits);
            if (grouping) {
                hits = cutFirstHits(hits, startHit);
                hitContainer = groupHits(query, hits, hitsPerPage, totalHits, startHit);
            } else {
                hits = cutHitsRight(hits, currentPage, hitsPerPage, startHit);
                hitContainer = new IngridHits(totalHits, hits);
            }
        }
        setDefaultInformations(hitContainer, resultSet, !query.isNotRanked());

        resultSet.clear();
        resultSet = null;

        if (fLogger.isDebugEnabled()) {
            fLogger.debug("search for: " + query.toString() + " startHit: " + startHit + " ended");
        }
        
        return hitContainer;
    }

    private void setDefaultInformations(IngridHits hitContainer, ResultSet resultSet, boolean ranked) {
        hitContainer.setPlugId("ibus");
        hitContainer.setInVolvedPlugs(resultSet.getPlugIdsWithResult().length);
        hitContainer.setRanked(ranked);
    }

    private ResultSet requestHits(IngridQuery query, int maxMilliseconds, PlugDescription[] plugsForQuery, int start,
            int requestLength) throws Exception {
        int plugsForQueryLength = plugsForQuery.length;
        ResultSet resultSet = new ResultSet(plugsForQueryLength);
        PlugQueryRequest[] requests = new PlugQueryRequest[plugsForQueryLength];

        for (int i = 0; i < plugsForQueryLength; i++) {
            PlugDescription plugDescription = plugsForQuery[i];
            IPlug plugProxy = this.fRegistry.getPlugProxy(plugDescription.getPlugId());
            if (plugProxy != null) {
                requests[i] = new PlugQueryRequest(plugProxy, this.fRegistry, plugDescription.getPlugId(), resultSet,
                        query, start, requestLength);
                requests[i].start();
            }
        }
        if (plugsForQueryLength > 0) {
            try {
                synchronized (resultSet) {
                    if (!resultSet.isComplete()) {
                        resultSet.wait(maxMilliseconds);
                    }
                }
            } catch (InterruptedException e) {
                if (fLogger.isWarnEnabled()) {
                    fLogger.warn("waiting for results iterrupted");
                }
            }
        }
        for (int i = 0; i < plugsForQueryLength; i++) {
            if (requests[i] != null) {
                requests[i].interrupt();
            }
            requests[i] = null; // for gc.
        }
        requests = null;

        return resultSet;
    }

    private IngridHits orderResults(ResultSet resultSet, PlugDescription[] plugDescriptionsForQuery) {
        int resultHitsCount = resultSet.size();
        int totalHits = 0;
        for (int i = 0; i < resultHitsCount; i++) {
            IngridHits hitContainer = (IngridHits) resultSet.get(i);
            int pos = getPlugPosition(plugDescriptionsForQuery, hitContainer.getPlugId());
            hitContainer.putInt(Comparators.UNRANKED_HITS_COMPARATOR_POSITION, pos);
            totalHits += hitContainer.length();
        }
        Collections.sort(resultSet, Comparators.UNRANKED_HITS_COMPARATOR);
        List orderedHits = new LinkedList();
        for (int i = 0; i < resultHitsCount; i++) {
            IngridHits hitContainer = (IngridHits) resultSet.get(i);
            IngridHit[] hits = hitContainer.getHits();
            if (hits != null && hits.length > 0) {
                orderedHits.addAll(Arrays.asList(hits));
            }

        }
        IngridHits result = new IngridHits(totalHits, (IngridHit[]) orderedHits.toArray(new IngridHit[orderedHits
                .size()]));

        orderedHits.clear();
        orderedHits = null;

        return result;
    }

    private int getPlugPosition(PlugDescription[] plugDescriptionsForQuery, String plugId) {
        for (int i = 0; i < plugDescriptionsForQuery.length; i++) {
            if (plugDescriptionsForQuery[i].getPlugId().equals(plugId)) {
                return i;
            }
        }
        if (fLogger.isWarnEnabled()) {
            fLogger.warn("plugId '" + plugId + "' not contained");
        }
        return Integer.MAX_VALUE;
    }

    private IngridHits normalizeScores(ArrayList resultSet) {
        float maxScore = 1.0f;
        int totalHits = 0;
        int count = resultSet.size();
        List documents = new LinkedList();
        boolean ranked = true;
        for (int i = 0; i < count; i++) {
            IngridHits hitContainer = (IngridHits) resultSet.get(i);
            totalHits += hitContainer.length();
            if (ranked) {
                ranked = hitContainer.isRanked();
                if (ranked && hitContainer.getHits().length > 0) {
                    Float boost = this.fRegistry.getGlobalRankingBoost(hitContainer.getPlugId());
                    IngridHit[] resultHits = hitContainer.getHits();
                    if (null != boost) {
                        for (int j = 0; j < resultHits.length; j++) {
                            float score = resultHits[j].getScore();
                            score = score * boost.floatValue();
                            hitContainer.getHits()[j].setScore(score);
                        }
                    }

                    if (maxScore < resultHits[0].getScore()) {
                        maxScore = resultHits[0].getScore();
                    }
                }

            }
            IngridHit[] toAddHits = hitContainer.getHits();
            if (toAddHits != null) {
                documents.addAll(Arrays.asList(toAddHits));
            }
        }

        IngridHits result = new IngridHits(totalHits, sortLimitNormalize((IngridHit[]) documents
                .toArray(new IngridHit[documents.size()]), ranked, maxScore));

        documents.clear();
        documents = null;

        return result;
    }

    private IngridHit[] sortLimitNormalize(IngridHit[] documents, boolean ranked, float maxScore) {
        if (!ranked) {
            return documents;
        }
        // first normalize
        float scoreNorm = 1.0f / maxScore;
        int count = documents.length;
        for (int i = 0; i < count; i++) {
            documents[i].setScore(documents[i].getScore() * scoreNorm);
        }

        Arrays.sort(documents, Comparators.SCORE_HIT_COMPARATOR);
        return documents;
    }

    private IngridHits groupHits(IngridQuery query, IngridHit[] hits, int hitsPerPage, int totalHits, int startHit)
            throws Exception {
        List groupHits = new ArrayList(hitsPerPage);
        int groupedHitsLength = 0;
        boolean newGroup;
        for (int i = 0; i < hits.length; i++) {
            IngridHit hit = hits[i];
            addGroupingInformation(hit, query);
            newGroup = true;
            int size = groupHits.size();
            for (int j = 0; j < size; j++) {
                IngridHit group = (IngridHit) groupHits.get(j);
                if (areInSameGroup(hit, group)) {
                    group.addGroupHit(hit);
                    newGroup = false;
                }
            }
            if (newGroup) {
                if (groupHits.size() < hitsPerPage) {
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

        return new IngridHits(totalHits, groupedHits, groupedHitsLength + startHit);
    }

    private void addGroupingInformation(IngridHit hit, IngridQuery query) throws Exception {
        if (hit.getGroupedFileds() != null) {
            return;
        }
        // XXX we just group for the 1st provider/partner
        if (IngridQuery.GROUPED_BY_PLUGID.equalsIgnoreCase(query.getGrouped())) {
            hit.addGroupedField(hit.getPlugId());
        } else if (IngridQuery.GROUPED_BY_PARTNER.equalsIgnoreCase(query.getGrouped())) {
            IPlug plug = this.fRegistry.getPlugProxy(hit.getPlugId());
            IngridHitDetail detail = plug.getDetail(hit, query, new String[] { PlugDescription.PARTNER });
            String[] partners = (String[]) detail.getArray(PlugDescription.PARTNER);
            for (int i = 0; partners != null && i < partners.length; i++) {
                hit.addGroupedField(partners[i]);
                break;
            }
        } else if (IngridQuery.GROUPED_BY_ORGANISATION.equalsIgnoreCase(query.getGrouped())) {
            IPlug plug = this.fRegistry.getPlugProxy(hit.getPlugId());
            IngridHitDetail detail = plug.getDetail(hit, query, new String[] { PlugDescription.PROVIDER });
            String[] providers = (String[]) detail.getArray(PlugDescription.PROVIDER);
            for (int i = 0; providers != null && i < providers.length; i++) {
                hit.addGroupedField(providers[i]);
                break;
            }
        } else {
            throw new IllegalArgumentException("unknown group operator '" + query.getGrouped() + '\'');
        }
        if (hit.getGroupedFileds() == null || hit.getGroupedFileds().length == 0) {
            hit.addGroupedField("no-detail-information:" + hit.getPlugId() + " (" + query.getGrouped() + ')');
            if (fLogger.isWarnEnabled()) {
                fLogger.warn("no-detail-information:" + hit.getPlugId() + " (" + query.getGrouped() + ')');
            }
        }
    }

    private boolean areInSameGroup(IngridHit group, IngridHit hit) {
        String[] groupFields = group.getGroupedFileds();
        String[] hitFields = hit.getGroupedFileds();
        for (int i = 0; i < groupFields.length; i++) {
            for (int j = 0; j < hitFields.length; j++) {
                if (groupFields[i].equalsIgnoreCase(hitFields[j])) {
                    return true;
                }
            }
        }
        return false;
    }

    private IngridHit[] cutHitsRight(IngridHit[] hits, int currentPage, int hitsPerPage, int startHit) {
        int pageStart = Math.min(((currentPage - 1) * hitsPerPage), hits.length);
        int resultLength = 0;
        if (hits.length <= pageStart) {
            final int preLastPage = hits.length / hitsPerPage;
            pageStart = Math.min((preLastPage * hitsPerPage), hits.length);
        }
        resultLength = Math.min(hits.length - pageStart, hitsPerPage);
        if (hits.length == resultLength) {
            return hits;
        }
        IngridHit[] cuttedHits = new IngridHit[resultLength];
        System.arraycopy(hits, pageStart, cuttedHits, 0, resultLength);

        return cuttedHits;
    }

    private IngridHit[] cutFirstHits(IngridHit[] hits, int startHit) {
        int newLength = hits.length - startHit;
        if (hits.length <= newLength) {
            return hits;
        }
        if (newLength < 1) {
            return new IngridHit[0];
        }
        IngridHit[] cuttedHits = new IngridHit[newLength];
        System.arraycopy(hits, startHit, cuttedHits, 0, newLength);
        return cuttedHits;
    }

    public Record getRecord(IngridHit hit) throws Exception {
        PlugDescription plugDescription = getIPlugRegistry().getPlugDescription(hit.getPlugId());
        IPlug plugProxy = this.fRegistry.getPlugProxy(hit.getPlugId());
        if (plugProxy == null) {
            throw new IllegalStateException("plug '" + hit.getPlugId() + "' currently not availible");
        }
        if (plugDescription.isRecordloader()) {
            return ((IRecordLoader) plugProxy).getRecord(hit);
        }
        if (fLogger.isWarnEnabled()) {
            fLogger.warn("plug does not implement record loader: " + plugDescription.getPlugId()
                    + " but was requested to load a record");
        }
        return null;
    }

    public IngridHitDetail getDetail(IngridHit hit, IngridQuery ingridQuery, String[] requestedFields) {
        if (requestedFields == null) {
            requestedFields = new String[0];
        }
        IPlug plugProxy = this.fRegistry.getPlugProxy(hit.getPlugId());
        try {
            IngridHitDetail detail = plugProxy.getDetail(hit, ingridQuery, requestedFields);
            pushMetaData(detail);
            return detail;
        } catch (Exception e) {
            if (fLogger.isErrorEnabled()) {
                fLogger.error(e.toString());
            }
        }

        return null;
    }

    public IngridHitDetail[] getDetails(IngridHit[] hits, IngridQuery query, String[] requestedFields) throws Exception {
        if (requestedFields == null) {
            requestedFields = new String[0];
        }
        // collect requests for plugs
        HashMap hashMap = new HashMap();
        IngridHit hit = null;
        for (int i = 0; i < hits.length; i++) {
            hit = hits[i];
            ArrayList requestHitList = (ArrayList) hashMap.get(hit.getPlugId());
            if (requestHitList == null) {
                requestHitList = new ArrayList();
                hashMap.put(hit.getPlugId(), requestHitList);
            }
            requestHitList.add(hit);
        }
        // send requests and collect response
        Iterator iterator = hashMap.keySet().iterator();
        IPlug plugProxy;
        ArrayList resultList = new ArrayList(hits.length);
        Random random = new Random(System.currentTimeMillis());
        while (iterator.hasNext()) {
            String plugId = (String) iterator.next();
            ArrayList requestHitList = (ArrayList) hashMap.get(plugId);
            if (requestHitList != null) {
                IngridHit[] requestHits = (IngridHit[]) requestHitList.toArray(new IngridHit[requestHitList.size()]);
                plugProxy = this.fRegistry.getPlugProxy(plugId);
                if (plugProxy != null) {
                    IngridHitDetail[] responseDetails = plugProxy.getDetails(requestHits, query, requestedFields);
                    for (int i = 0; i < responseDetails.length; i++) {
                        if (responseDetails[i] == null) {
                            if (fLogger.isErrorEnabled()) {
                                fLogger
                                        .error(plugId
                                                + ": responded details that are null (set a pseudo responseDetail");
                            }
                            responseDetails[i] = new IngridHitDetail(plugId, random.nextInt(), random.nextInt(), 0.0f,
                                    "", "");
                        }
                    }

                    resultList.addAll(Arrays.asList(responseDetails));
                    // FIXME: to improve performance we can use an Array instead of a list here.
                }
            }

            if (null != requestHitList) {
                requestHitList.clear();
                requestHitList = null;
            }
        }

        hashMap.clear();
        hashMap = null;

        // int count = resultList.size();
        IngridHitDetail[] resultDetails = (IngridHitDetail[]) resultList
                .toArray(new IngridHitDetail[resultList.size()]);

        resultList.clear();
        resultList = null;

        // sort to be in the same order as the requested hits.
        IngridHitDetail[] details = new IngridHitDetail[hits.length];
        for (int i = 0; i < hits.length; i++) {
            String plugId = hits[i].getPlugId();
            int documentId = hits[i].getDocumentId();

            boolean found = false;

            for (int j = 0; j < resultDetails.length; j++) {
                IngridHitDetail detail = resultDetails[j];
                if (detail.getDocumentId() == documentId && detail.getPlugId().equals(plugId)) {
                    details[i] = detail;
                    pushMetaData(details[i]); // push meta data to details
                    found = true;
                }
            }
            if (!found) {
                if (fLogger.isErrorEnabled()) {
                    fLogger.error("unable to find details getDetails: " + hit.toString());
                }
                details[i] = new IngridHitDetail(hit, "no details found", "");
            }
        }
        return details;
    }

    private void pushMetaData(IngridHitDetail detail) {
        PlugDescription plugDescription;
        plugDescription = this.fRegistry.getPlugDescription(detail.getPlugId());
        detail.setOrganisation(plugDescription.getOrganisation());
        detail.setDataSourceName(plugDescription.getDataSourceName());
        detail.setIplugClassName(plugDescription.getIPlugClass());

    }

    /**
     * A pipe with pre process and post process functionality for a query. Every query goes through the posst process
     * pipe before the search and the pre process pipe after the search.
     * 
     * @return The processing pipe.
     */
    public ProcessorPipe getProccessorPipe() {
        return this.fProcessorPipe;
    }

    /**
     * The registry for all iplugs.
     * 
     * @return The iplug registry.
     */
    public Registry getIPlugRegistry() {
        return this.fRegistry;
    }

    public boolean containsPlugDescription(String plugId, String md5Hash) {
        return this.fRegistry.containsPlugDescription(plugId, md5Hash);
    }

    public void addPlugDescription(PlugDescription plugDescription) {
        if (null != plugDescription) {
            if (fLogger.isInfoEnabled()) {
                fLogger.info("adding or updating plug '" + plugDescription.getPlugId() + "' current plug count:"
                        + getAllIPlugs().length);
            }
            this.fRegistry.addPlugDescription(plugDescription);
        } else {
            if (fLogger.isErrorEnabled()) {
                fLogger.error("Cannot add IPlug: plugdescription is null.");
            }
        }
    }

    public void removePlugDescription(PlugDescription plugDescription) {
        if (fLogger.isInfoEnabled()) {
            fLogger.info("removing plug '" + plugDescription.getPlugId() + "' current plug count:"
                    + getAllIPlugs().length);
        }
        this.fRegistry.removePlug(plugDescription.getPlugId());
    }

    public PlugDescription[] getAllIPlugs() {
        return this.fRegistry.getAllIPlugs();
    }
    
    public PlugDescription[] getAllIPlugsWithoutTimeLimitation() {
        return this.fRegistry.getAllIPlugsWithoutTimeLimitation();
    }

    public PlugDescription getIPlug(String plugId) {
        return this.fRegistry.getPlugDescription(plugId);
    }

    public void close() throws Exception {
        // nothing
    }
}
