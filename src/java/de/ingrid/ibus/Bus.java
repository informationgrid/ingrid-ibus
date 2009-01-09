/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
import de.ingrid.utils.metadata.Metadata;
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

    private IGrouper _grouper;
    
    private ProcessorPipe fProcessorPipe = new ProcessorPipe();

	private Metadata _metadata;

    /**
     * The bus. All IPlugs have to connect with the bus to be searched. It sends queries to registered and activated
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
        _grouper = new Grouper(this.fRegistry);
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

    public IngridHits search(IngridQuery query, int hitsPerPage, int currentPage, int startHit,
            int maxMilliseconds) throws Exception {
        if (fLogger.isDebugEnabled()) {
            fLogger.debug("search for: " + query.toString() + " startHit: " + startHit + " started");
        }
        if (currentPage < 1) {
            currentPage = 1;
        }
        this.fProcessorPipe.preProcess(query);
        boolean grouping = query.getGrouped() != null &&
                !query.getGrouped().equalsIgnoreCase(IngridQuery.GROUPED_OFF);

        if (fLogger.isDebugEnabled()) {
            fLogger.debug("Grouping: " + grouping);
        }
        int requestLength;
        if (!grouping) {
            requestLength = hitsPerPage * currentPage;
        } else {
            requestLength = startHit + (hitsPerPage * 6);
        }

        PlugDescription[] plugDescriptionsForQuery = SyntaxInterpreter.getIPlugsForQuery(query, this.fRegistry);
        boolean oneIPlugOnly = (plugDescriptionsForQuery.length == 1);
        boolean forceManyResults = grouping && (oneIPlugOnly && "de.ingrid.iplug.se.NutchSearcher".equals(plugDescriptionsForQuery[0].getIPlugClass())); 
        ResultSet resultSet;
        if (!oneIPlugOnly) {
            if (fLogger.isDebugEnabled()) {
                logDebug("(search) request starts: " + query.hashCode());
            }
            resultSet = requestHits(query, maxMilliseconds, plugDescriptionsForQuery, 0, requestLength);
            if (fLogger.isDebugEnabled()) {
                logDebug("(search) request ends: " + query.hashCode());
            }
        } else {
            // request only one iplug! request from "startHit" position with
            // length "hitsPerPage", because no ranking is required
            if (fLogger.isDebugEnabled()) {
                fLogger.debug("search for: " + query.toString() + " startHit: " + startHit + " started");
            }
            resultSet = requestHits(query, maxMilliseconds, plugDescriptionsForQuery, startHit, forceManyResults ? hitsPerPage*6 : hitsPerPage);
        }

        IngridHits hitContainer;
        if (query.isNotRanked()) {
            if (fLogger.isDebugEnabled()) {
                logDebug("(search) order starts: " + query.hashCode());
            }
            hitContainer = orderResults(resultSet, plugDescriptionsForQuery);
            if (fLogger.isDebugEnabled()) {
                logDebug("(search) order ends: " + query.hashCode());
            }
        } else {
            if (fLogger.isDebugEnabled()) {
                logDebug("(search) normalize starts: " + query.hashCode());
            }
            hitContainer = normalizeScores(resultSet);
            if (fLogger.isDebugEnabled()) {
                logDebug("(search) normalize ends: " + query.hashCode());
            }
        }

		IngridHit[] hits = hitContainer.getHits();
		int oldSize = hits.length;
		// remove duplicates after normalizing and ordering to keep duplicates
		// with highest score
		Set set = new LinkedHashSet();
		for (int i = 0; i < hits.length; i++) {
			set.add(hits[i]);
		}
		hitContainer = new IngridHits(set.size(), (IngridHit[]) set
				.toArray(new IngridHit[set.size()]));
		hits = hitContainer.getHits();
		// re-search if duplicates are removed
		if (oldSize > hits.length) {
			// re-search recursiv but only 3 times, (hitsPerPage = 60, 120, 240)
			if (hits.length < hitsPerPage && hitsPerPage < 300) {
				this.fLogger.info("research with hitsPerPage: " + hitsPerPage
						* 2);
				search(query, hitsPerPage * 2, currentPage, startHit,
						maxMilliseconds);
			}
		}
		
		
        int totalHits = (int) hitContainer.length();
        if (hits.length > 0) {
            this.fProcessorPipe.postProcess(query, hits);
            if (grouping) {
                // prevent array cutting with only one requested iplug, assuming
                // we already have the right number of hits in the result array
                if (!oneIPlugOnly) {
                    hits = cutFirstHits(hits, startHit);
                }
                if(fLogger.isDebugEnabled()) {
                    logDebug("(search) grouping starts: " + query.hashCode());
                }
                hitContainer = _grouper.groupHits(query, hits, hitsPerPage, totalHits, startHit);
                if(fLogger.isDebugEnabled()) {
                    logDebug("(search) grouping ends: " + query.hashCode());
                }
            } else {
                // prevent array cutting with only one requested iplug, assuming
                // we already have the right number of hits in the result array
                if (!oneIPlugOnly) {
                    hits = cutHitsRight(hits, currentPage, hitsPerPage, startHit);
                }
                hitContainer = new IngridHits(totalHits, hits);
            }
        }
        setDefaultInformations(hitContainer, resultSet, !query.isNotRanked());

        resultSet.clear();
        resultSet = null;

        if (fLogger.isDebugEnabled()) {
            fLogger.debug("search for: " + query.toString() + " startHit: " + startHit + " ended");

            IngridHit[] ingridHits = hitContainer.getHits();
            for (int i = 0; i < ingridHits.length; i++) {
                IngridHit ingridHit = ingridHits[i];
                fLogger.debug("documentId: " + ingridHit.getDocumentId() + " score: " + ingridHit.getScore());
            }
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
        if (fLogger.isDebugEnabled()) {
            fLogger.debug("order the results");
        }

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
        if (fLogger.isDebugEnabled()) {
            fLogger.debug("normalize the results");
        }

        float maxScore = 1.0f;
        int totalHits = 0;
        int count = resultSet.size();
        List documents = new LinkedList();
        for (int i = 0; i < count; i++) {
            IngridHits hitContainer = (IngridHits) resultSet.get(i);
            totalHits += hitContainer.length();
            if (hitContainer.getHits().length > 0) {
                Float boost = this.fRegistry.getGlobalRankingBoost(hitContainer.getPlugId());
                IngridHit[] resultHits = hitContainer.getHits();
                if (null != boost) {
                    for (int j = 0; j < resultHits.length; j++) {
                        float score = 1.0f;
                        if (hitContainer.isRanked()) {
                            score = resultHits[j].getScore();
                            score = score * boost.floatValue();
                        }
                        hitContainer.getHits()[j].setScore(score);
                    }
                }

                if (maxScore < resultHits[0].getScore()) {
                    maxScore = resultHits[0].getScore();
                }
            }

            IngridHit[] toAddHits = hitContainer.getHits();
            if (toAddHits != null) {
                documents.addAll(Arrays.asList(toAddHits));
            }
        }

        IngridHits result = new IngridHits(totalHits, sortLimitNormalize((IngridHit[]) documents
                .toArray(new IngridHit[documents.size()]), maxScore));

        documents.clear();
        documents = null;

        return result;
    }

    private IngridHit[] sortLimitNormalize(IngridHit[] documents, float maxScore) {
        // first normalize
        float scoreNorm = 1.0f / maxScore;
        int count = documents.length;
        for (int i = 0; i < count; i++) {
            if (fLogger.isDebugEnabled()) {
                fLogger.debug("documentScore: " + documents[i].getPlugId() + " -> " + documents[i].getScore() +
                        " scoreNorm: " + scoreNorm + " = " + documents[i].getScore() * scoreNorm);
            }
            documents[i].setScore(documents[i].getScore() * scoreNorm);
        }

        Arrays.sort(documents, Comparators.SCORE_HIT_COMPARATOR);
        return documents;
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
            fLogger.warn("plug does not implement record loader: " + plugDescription.getPlugId() +
                    " but was requested to load a record");
        }
        return null;
    }

    public IngridHitDetail getDetail(IngridHit hit, IngridQuery ingridQuery, String[] requestedFields) {
        if (requestedFields == null) {
            requestedFields = new String[0];
        }
        IPlug plugProxy = this.fRegistry.getPlugProxy(hit.getPlugId());
        try {
            logDebug("(search) detail start " + hit.getPlugId() + " " + ingridQuery.hashCode());
            IngridHitDetail detail = plugProxy.getDetail(hit, ingridQuery, requestedFields);
            logDebug("(search) detail end " + hit.getPlugId() + " " + ingridQuery.hashCode());
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
                    logDebug("(search) details start " + plugId + " (" + requestHits.length + ") " + query.hashCode());
                    IngridHitDetail[] responseDetails = plugProxy.getDetails(requestHits, query, requestedFields);
                    logDebug("(search) details ends (" + responseDetails.length + ")" + plugId + " " + query.hashCode());
                    for (int i = 0; i < responseDetails.length; i++) {
                        if (responseDetails[i] == null) {
                            if (fLogger.isErrorEnabled()) {
                                fLogger
                                        .error(plugId +
                                                ": responded details that are null (set a pseudo responseDetail");
                            }
                            responseDetails[i] = new IngridHitDetail(plugId, random.nextInt(), random.nextInt(), 0.0f,
                                    "", "");
                        }
                    }

                    resultList.addAll(Arrays.asList(responseDetails));
                    // FIXME: to improve performance we can use an Array instead
                    // of a list here.
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
                fLogger.info("adding or updating plug '" + plugDescription.getPlugId() + "' current plug count:" +
                        getAllIPlugs().length);
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
            fLogger.info("removing plug '" + plugDescription.getPlugId() + "' current plug count:" +
                    getAllIPlugs().length);
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

    private void logDebug(String string) {
        if (fLogger.isDebugEnabled()) {
            fLogger.debug(string);
        }
    }

	@Override
	public Serializable getMetadata(String plugId, String metadataKey) {
		Metadata metadata = getMetadata(plugId);
		return metadata != null ? metadata.getMetadata(metadataKey) : null;
	}
	
	@Override
	public Metadata getMetadata(String plugId) {
		PlugDescription plugDescription = getIPlug(plugId);
		return plugDescription != null ? plugDescription.getMetadata() : null;
	}
	
	@Override
	public Metadata getMetadata() {
		return _metadata;
	}
	
	public void setMetadata(Metadata metadata) {
		_metadata = metadata;
	}
	
	public IngridHitDetail[] searchAndDetail(IngridQuery query,
			int hitsPerPage,
			int currentPage, int startHit, int maxMilliseconds,
			String[] requestedFields) throws Exception {

		IngridHits searchedHits = search(query, hitsPerPage, currentPage,
				startHit,
				maxMilliseconds);
		IngridHit[] hits = searchedHits.getHits();
		IngridHitDetail[] details = getDetails(hits, query, requestedFields);
		return details;
	}
}
