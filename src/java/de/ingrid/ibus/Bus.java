/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import java.io.IOException;
import java.io.InputStream;
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
import de.ingrid.utils.config.Configuration;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.processor.ProcessorPipe;
import de.ingrid.utils.query.IngridQuery;

/**
 * The IBus a centralized Bus that routes queries and return results. Created on
 * 09.08.2005
 * 
 * @author sg
 * @version $Revision: 1.3 $
 */
public class Bus extends Thread implements IBus {

    private static final long serialVersionUID = Bus.class.getName().hashCode();

    private static Log fLogger = LogFactory.getLog(Bus.class);

    // TODO INGRID-398 we need to made the lifetime configurable.
    private Registry fRegistry;

    private ProcessorPipe fProcessorPipe = new ProcessorPipe();

    private IPlugProxyFactory fProxyFactory = null;

    /**
     * @param factory
     */
    public Bus(IPlugProxyFactory factory) {
        this.fProxyFactory = factory;
        boolean iplugAutoActivation = getAutoActivationProperty();
        this.fRegistry = new Registry(100000, iplugAutoActivation);
    }

    public IngridHits search(IngridQuery query, final int hitsPerPage, int currentPage, int startHit,
            int maxMilliseconds) throws Exception {
        if (fLogger.isDebugEnabled()) {
            fLogger.debug("search for: " + query.toString());
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
            requestLength = startHit + (hitsPerPage * 3);
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
        return hitContainer;
    }

    private void setDefaultInformations(IngridHits hitContainer, ResultSet resultSet, boolean ranked) {
        hitContainer.setPlugId("ibus");
        hitContainer.setInVolvedPlugs(resultSet.getPlugIdsWithResult().length);
        hitContainer.setRanked(ranked);
        System.out.println("groupleng:"+hitContainer.getGoupedHitsLength());
    }

    private ResultSet requestHits(IngridQuery query, int maxMilliseconds, PlugDescription[] plugsForQuery, int start,
            int requestLength) throws Exception {
        int plugsForQueryLength = plugsForQuery.length;
        ResultSet resultSet = new ResultSet(plugsForQueryLength);
        PlugQueryRequest[] requests = new PlugQueryRequest[plugsForQueryLength];

        for (int i = 0; i < plugsForQueryLength; i++) {
            PlugDescription plugDescription = plugsForQuery[i];
            IPlug plugProxy = getPlugProxy(plugDescription.getPlugId());
            requests[i] = new PlugQueryRequest(plugProxy, this.fRegistry, plugDescription.getPlugId(), resultSet,
                    query, start, requestLength);
            requests[i].start();
        }
        if (plugsForQueryLength > 0) {
            try {
                synchronized (resultSet) {
                    if (!resultSet.isComplete()) {
                        resultSet.wait(maxMilliseconds);
                    }
                }
            } catch (InterruptedException e) {
                fLogger.warn("waiting for results iterrupted");
            }
        }
        for (int i = 0; i < plugsForQueryLength; i++) {
            if (requests[i] != null) {
                requests[i].interrupt();
            }
            requests[i] = null; // for gc.
        }
        return resultSet;
    }

    private IngridHits orderResults(ResultSet resultSet, PlugDescription[] plugDescriptionsForQuery) {
        int resultHitsCount = resultSet.size();
        for (int i = 0; i < resultHitsCount; i++) {
            IngridHits hitContainer = (IngridHits) resultSet.get(i);
            int pos = getPlugPosition(plugDescriptionsForQuery, hitContainer.getPlugId());
            hitContainer.putInt(Comparators.UNRANKED_HITS_COMPARATOR_POSITION, pos);
        }
        Collections.sort(resultSet, Comparators.UNRANKED_HITS_COMPARATOR);
        List orderedHits = new LinkedList();
        for (int i = 0; i < resultHitsCount; i++) {
            IngridHits hitContainer = (IngridHits) resultSet.get(i);
            orderedHits.addAll(Arrays.asList(hitContainer.getHits()));
        }
        return new IngridHits(orderedHits.size(), (IngridHit[]) orderedHits.toArray(new IngridHit[orderedHits.size()]));
    }

    private int getPlugPosition(PlugDescription[] plugDescriptionsForQuery, String plugId) {
        for (int i = 0; i < plugDescriptionsForQuery.length; i++) {
            if (plugDescriptionsForQuery[i].getPlugId().equals(plugId)) {
                return i;
            }
        }
        fLogger.warn("plug id not contained");
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

        return new IngridHits(totalHits, sortLimitNormalize((IngridHit[]) documents.toArray(new IngridHit[documents
                .size()]), ranked, maxScore));
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
        return new IngridHits(totalHits, groupedHits, groupedHitsLength + startHit);
    }

    private void addGroupingInformation(IngridHit hit, IngridQuery query) throws Exception {
        // XXX we just group for the 1st provider/partner
        if (IngridQuery.GROUPED_BY_PLUGID.equalsIgnoreCase(query.getGrouped())) {
            hit.addGroupedField(hit.getPlugId());
        } else if (IngridQuery.GROUPED_BY_PARTNER.equalsIgnoreCase(query.getGrouped())) {
            IPlug plug = getPlugProxy(hit.getPlugId());
            IngridHitDetail detail = plug.getDetail(hit, query, new String[] { PlugDescription.PARTNER });
            String[] partners = (String[]) detail.getArray(PlugDescription.PARTNER);
            for (int i = 0; i < partners.length; i++) {
                hit.addGroupedField(partners[i]);
                break;
            }
        } else if (IngridQuery.GROUPED_BY_ORGANISATION.equalsIgnoreCase(query.getGrouped())) {
            IPlug plug = getPlugProxy(hit.getPlugId());
            IngridHitDetail detail = plug.getDetail(hit, query, new String[] { PlugDescription.PROVIDER });
            String[] providers = (String[]) detail.getArray(PlugDescription.PROVIDER);
            for (int i = 0; i < providers.length; i++) {
                hit.addGroupedField(providers[i]);
                break;
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
        if (hits.length == pageStart) {
            pageStart = Math.min(((currentPage - 2) * hitsPerPage), hits.length);
        }
        if (hits.length > pageStart) {
            resultLength = Math.min(hits.length - pageStart, hitsPerPage);
        } else {
            resultLength = Math.min(hits.length, hitsPerPage);
        }
        if (hits.length == resultLength) {
            return hits;
        }
        IngridHit[] cuttedHits = new IngridHit[resultLength];
        System.arraycopy(hits, pageStart, cuttedHits, 0, resultLength);
        // System.out.println("hits: " + hits.length);
        // System.out.println("pageStart: " + pageStart);
        // System.out.println("newHi"+newHits.length);
        // System.out.println("resultlenght: " + resultLength);s
        return cuttedHits;
    }

    private IngridHit[] cutFirstHits(IngridHit[] hits, int startHit) {
        int newLength = hits.length - startHit;
        if (hits.length <= newLength) {
            return hits;
        }
        IngridHit[] cuttedHits = new IngridHit[newLength];
        System.arraycopy(hits, startHit, cuttedHits, 0, newLength);
        return cuttedHits;
    }

    public Record getRecord(IngridHit hit) throws Exception {
        PlugDescription plugDescription = getIPlugRegistry().getPlugDescription(hit.getPlugId());
        IPlug plugProxy = getPlugProxy(hit.getPlugId());
        if (plugDescription.isRecordloader()) {
            return ((IRecordLoader) plugProxy).getRecord(hit);
        }
        fLogger.warn("plug does not implement record loader: " + plugDescription.getPlugId()
                + " but was requested to load a record");
        return null;
    }

    /**
     * @param hit
     * @return A detailed document of a hit.
     * @throws Exception
     */
    public IngridHitDetail getDetail(IngridHit hit, IngridQuery ingridQuery, String[] requestedFields) throws Exception {
        if (requestedFields == null) {
            requestedFields = new String[0];
        }
        IPlug plugProxy = getPlugProxy(hit.getPlugId());
        try {
            IngridHitDetail detail = plugProxy.getDetail(hit, ingridQuery, requestedFields);
            pushMetaData(detail);
            return detail;
        } catch (Exception e) {
            fLogger.error(e.toString());
        }
        // FIXME do we still need to announce any exception in the method
        // signature now?
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
                plugProxy = getPlugProxy(plugId);
                IngridHitDetail[] responseDetails = plugProxy.getDetails(requestHits, query, requestedFields);
                for (int i = 0; i < responseDetails.length; i++) {
                    if (responseDetails[i] == null) {
                        fLogger.error(plugId + ": responded details that are null (set a pseudo responseDetail");
                        responseDetails[i] = new IngridHitDetail(plugId, random.nextInt(), random.nextInt(), 0.0f, "",
                                "");
                    }
                }

                resultList.addAll(Arrays.asList(responseDetails)); // FIXME to
                // improve performance we can use an Array instead of a list
                // here.
            }
        }

        // int count = resultList.size();
        IngridHitDetail[] resultDetails = (IngridHitDetail[]) resultList
                .toArray(new IngridHitDetail[resultList.size()]);
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
                fLogger.error("unablefind details getDetais: " + hit.toString());
                details[i] = new IngridHitDetail(hit, "no details found", ""); // FIXME
            }
        }
        return details;
    }

    private void pushMetaData(IngridHitDetail detail) {
        PlugDescription plugDescription = this.fRegistry.getPlugDescription(detail.getPlugId());
        detail.setOrganisation(plugDescription.getOrganisation());
        detail.setDataSourceName(plugDescription.getDataSourceName());
        detail.setIplugClassName(plugDescription.getIPlugClass());

    }

    /**
     * @param plugId
     * @return a proxy from cache or if it was not cached we create a new proxy
     *         and add it to the cache.
     */
    private IPlug getPlugProxy(String plugId) {
        IPlug plugProxy = this.fRegistry.getProxyFromCache(plugId);
        if (null == plugProxy) {
            PlugDescription plugDescription = getIPlugRegistry().getPlugDescription(plugId);
            fLogger.debug("Create new connection to IPlug: " + plugDescription.getPlugId());
            try {
                plugProxy = this.fProxyFactory.createPlugProxy(plugDescription);
                this.fRegistry.addProxyToCache(plugDescription.getPlugId(), plugProxy);
            } catch (Exception e) {
                fLogger.error("(REMOVING IPLUG '" + plugId + "' !): could not creat proxy object: ", e);
                removePlugDescription(plugDescription);
            }

        }
        return plugProxy;
    }

    /**
     * @return The processing pipe.
     */
    public ProcessorPipe getProccessorPipe() {
        return this.fProcessorPipe;
    }

    /**
     * @return The iplug registry.
     */
    public Registry getIPlugRegistry() {
        return this.fRegistry;
    }

    public void addPlugDescription(PlugDescription plugDescription) {
        this.fRegistry.addIPlug(plugDescription);
    }

    public void removePlugDescription(PlugDescription plugDescripion) {
        this.fRegistry.removePlugFromCache(plugDescripion.getPlugId());
    }

    public PlugDescription[] getAllIPlugs() {
        return this.fRegistry.getAllIPlugs();
    }

    public PlugDescription getIPlug(String plugId) {
        return this.fRegistry.getPlugDescription(plugId);
    }

    private boolean getAutoActivationProperty() {
        Configuration configuration = new Configuration();
        InputStream resourceAsStream = Bus.class.getResourceAsStream("/configuration.xml");
        boolean iplugAutoActivation = true;
        if (resourceAsStream != null) {
            try {
                configuration.load(resourceAsStream);
            } catch (IOException e) {
                fLogger.error("unable to load existing configuration", e);
            }
            iplugAutoActivation = configuration.get("iplugAutoActivation", "false").equals("true");
        }
        return iplugAutoActivation;
    }

    public void close() throws Exception {
        // nothing
    }
}
