/*
 * **************************************************-
 * InGrid iBus
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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
/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.comm;

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
import java.util.concurrent.Future;

import de.ingrid.ibus.management.ManagementService;
import de.ingrid.ibus.service.SearchService;
import de.ingrid.ibus.service.SettingsService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.ibus.comm.debug.DebugEvent;
import de.ingrid.ibus.comm.debug.DebugQuery;
import de.ingrid.ibus.comm.net.IPlugProxyFactory;
import de.ingrid.ibus.comm.net.PlugQueryRequest;
import de.ingrid.ibus.comm.registry.Registry;
import de.ingrid.ibus.comm.registry.SyntaxInterpreter;
import de.ingrid.utils.IBus;
import de.ingrid.utils.IPlug;
import de.ingrid.utils.IRecordLoader;
import de.ingrid.utils.IngridCall;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.metadata.Metadata;
import de.ingrid.utils.processor.ProcessorPipe;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.tool.PlugDescriptionUtil;
import de.ingrid.utils.tool.QueryUtil;
import net.weta.components.communication.tcp.TimeoutException;
import net.weta.components.communication.util.PooledThreadExecutor;

/**
 * The IBus a centralized Bus that routes queries and return results. Created on
 * 09.08.2005
 * 
 * @author sg
 * @version $Revision: 1.3 $
 */
public class Bus extends Thread implements IBus {

    private static final long serialVersionUID = Bus.class.getName().hashCode();

    private static Log fLogger = LogFactory.getLog( Bus.class );

    private static Bus fInstance;

    private final SettingsService settingsService;

    // TODO INGRID-398 we need to made the lifetime configurable.
    private Registry fRegistry;

    private IGrouper _grouper;

    private ProcessorPipe fProcessorPipe = new ProcessorPipe();

    private Metadata _metadata;

    private DebugQuery debug;

    /**
     * The bus. All IPlugs have to connect with the bus to be searched. It sends
     * queries to registered and activated iplugs. It only sends a query to a
     * iplug if it is able to handle the query. For all implemented criteria see
     * de.ingrid.ibus.registry.SyntaxInterpreter#getIPlugsForQuery(IngridQuery,
     * Registry) .
     * 
     * @param factory
     *            A factroy for creating iplug proxies.
     * @see de.ingrid.ibus.comm.registry.SyntaxInterpreter#getIPlugsForQuery(IngridQuery,
     *      Registry)
     */
    public Bus(IPlugProxyFactory factory, SettingsService settingsService) {
        this.fRegistry = new Registry( 120000, false, factory );
        fInstance = this;
        this.settingsService = settingsService;
        _grouper = new Grouper( this.fRegistry );
        debug = new DebugQuery();
        SyntaxInterpreter.debug = this.debug;
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

    public IngridHits search(IngridQuery query, int hitsPerPage, int currentPage, int startHit, int maxMilliseconds) throws Exception {
        long startSearch = 0;
        if (fLogger.isDebugEnabled()) {
            startSearch = System.currentTimeMillis();
        }
        if (fLogger.isDebugEnabled()) {
            fLogger.debug( "search for: " + query.toString() + "(" + query.hashCode() + ") startHit: " + startHit + "; timeout: " + maxMilliseconds + "ms ->  started" );
        }
        if (currentPage < 1) {
            currentPage = 1;
        }
        this.fProcessorPipe.preProcess( query );
        boolean grouping = query.getGrouped() != null && !query.getGrouped().equalsIgnoreCase( IngridQuery.GROUPED_OFF );

        if (fLogger.isDebugEnabled()) {
            fLogger.debug( "Grouping: " + grouping );
        }
        int requestLength;
        if (!grouping) {
            requestLength = hitsPerPage * currentPage;
        } else {
            requestLength = startHit + (hitsPerPage * 6);
        }

        PlugDescription[] plugDescriptionsForQuery = SyntaxInterpreter.getIPlugsForQuery( query, this.fRegistry );
        boolean oneIPlugOnly = (plugDescriptionsForQuery.length == 1);
        boolean forceManyResults = grouping
                && (oneIPlugOnly && ("de.ingrid.iplug.se.NutchSearcher".equalsIgnoreCase( plugDescriptionsForQuery[0].getIPlugClass() ) || "de.ingrid.iplug.se.seiplug"
                        .equalsIgnoreCase( plugDescriptionsForQuery[0].getIPlugClass() )));
        ResultSet resultSet;
        if (!oneIPlugOnly) {
            if (fLogger.isDebugEnabled()) {
                fLogger.debug( "(search) request starts: " + query.hashCode() );
            }
            resultSet = requestHits( query, maxMilliseconds, plugDescriptionsForQuery, 0, requestLength );
            if (fLogger.isDebugEnabled()) {
                fLogger.debug( "(search) request ends: " + query.hashCode() );
            }
        } else {
            // request only one iplug! request from "startHit" position with
            // length "hitsPerPage", because no ranking is required
            if (fLogger.isDebugEnabled()) {
                fLogger.debug( "search for: " + query.toString() + "(" + query.hashCode() + " startHit: " + startHit + " started" );
            }
            resultSet = requestHits( query, maxMilliseconds, plugDescriptionsForQuery, startHit, forceManyResults ? hitsPerPage * 6 : hitsPerPage );
        }

        if (debug.isActive( query )) {
            Iterator<IngridHits> it = resultSet.iterator();
            while (it.hasNext()) {
                IngridHits hits = it.next();
                DebugEvent event = new DebugEvent( "Hits from '" + hits.getPlugId() + "'", "" + hits.length() );
                event.duration = hits.getSearchTimings().get( hits.getPlugId() );
                debug.addEvent( event );
            }
        }

        IngridHits hitContainer;
        if (query.isNotRanked()) {
            if (fLogger.isDebugEnabled()) {
                fLogger.debug( "(search) order starts: " + query.hashCode() );
            }
            hitContainer = orderResults( resultSet, plugDescriptionsForQuery, query );
            if (fLogger.isDebugEnabled()) {
                fLogger.debug( "(search) order ends: " + query.hashCode() );
            }
        } else {
            if (fLogger.isDebugEnabled()) {
                fLogger.debug( "(search) normalize starts: " + query.hashCode() );
            }
            // normalize only if there were more than one iplugs queried
            // if we only query one, than it doesn't matter how high the score
            // is
            // since we don't need to merge the results with other ones
            hitContainer = normalizeScores( resultSet, oneIPlugOnly ? true : false );

            if (fLogger.isDebugEnabled()) {
                fLogger.debug( "(search) normalize ends: " + query.hashCode() );
            }
        }

        IngridHit[] hits = hitContainer.getHits();
        int oldSize = hits.length;
        // remove duplicates after normalizing and ordering to keep duplicates
        // with highest score
        Set set = new LinkedHashSet( Arrays.asList( hits ) );
        hitContainer = new IngridHits( (int) hitContainer.length(), (IngridHit[]) set.toArray( new IngridHit[set.size()] ) );
        hits = hitContainer.getHits();
        // re-search if duplicates are removed
        if (oldSize > hits.length) {
            // re-search recursiv but only 3 times, (hitsPerPage = 60, 120, 240)
            if (hits.length < hitsPerPage && hitsPerPage < 300) {
                fLogger.info( "research with hitsPerPage: " + hitsPerPage * 2 );
                hitContainer = search( query, hitsPerPage * 2, currentPage, startHit, maxMilliseconds );
                hits = hitContainer.getHits();
            }
        }

        int totalHits = (int) hitContainer.length();
        if (hits.length > 0) {
            this.fProcessorPipe.postProcess( query, hits );
            if (grouping) {
                // prevent array cutting with only one requested iplug, assuming
                // we already have the right number of hits in the result array
                if (!oneIPlugOnly) {
                    hits = cutFirstHits( hits, startHit );
                }
                if (fLogger.isDebugEnabled()) {
                    fLogger.debug( "(search) grouping starts: " + query.hashCode() );
                }
                hitContainer = _grouper.groupHits( query, hits, hitsPerPage, totalHits, startHit, resultSet );
                if (fLogger.isDebugEnabled()) {
                    fLogger.debug( "(search) grouping ends: " + query.hashCode() );
                }
            } else {
                // prevent array cutting with only one requested iplug, assuming
                // we already have the right number of hits in the result array
                if (!oneIPlugOnly) {
                    hits = cutHitsRight( hits, currentPage, hitsPerPage, startHit );
                }
                hitContainer = new IngridHits( totalHits, hits );
            }
        }
        setDefaultInformations( hitContainer, resultSet, !query.isNotRanked() );

        addFacetInfo( hitContainer, resultSet );

        resultSet.clear();
        resultSet = null;

        if (fLogger.isDebugEnabled()) {
            fLogger.debug( "search for: " + query.toString() + "(" + query.hashCode() + " startHit: " + startHit + " ended" );

            IngridHit[] ingridHits = hitContainer.getHits();
            for (int i = 0; i < ingridHits.length; i++) {
                IngridHit ingridHit = ingridHits[i];
                fLogger.debug( "documentId: " + ingridHit.getDocumentId() + " score: " + ingridHit.getScore() );
            }
        }
        if (fLogger.isDebugEnabled()) {
            fLogger.debug( "TIMING: Search for Query (" + query.hashCode() + ") took " + (System.currentTimeMillis() - startSearch) + "ms." );
        }

        return hitContainer;
    }

    @SuppressWarnings("unchecked")
    private void addFacetInfo(IngridHits hitContainer, ResultSet resultSet) {
        IngridDocument allFacetClasses = null;
        for (IngridHits hits : (ArrayList<IngridHits>) resultSet) {
            if (fLogger.isDebugEnabled()) {
                fLogger.debug( "Add facets for iPlug: " + hits.getPlugId() );
            }
            IngridDocument facetClasses = (IngridDocument) hits.get( "FACETS" );
            if (facetClasses != null && facetClasses.size() > 0) {
                if (allFacetClasses == null) {
                    allFacetClasses = new IngridDocument();
                    allFacetClasses.putAll( facetClasses );
                } else {
                    for (Object o : facetClasses.keySet()) {
                        String facetClassString = (String) o;
                        if (allFacetClasses.containsKey( facetClassString )) {
                            allFacetClasses.put( facetClassString, allFacetClasses.getLong( facetClassString ) + facetClasses.getLong( facetClassString ) );
                        } else {
                            allFacetClasses.put( facetClassString, facetClasses.getLong( facetClassString ) );
                        }
                    }
                }
                if (fLogger.isDebugEnabled()) {
                    for (Object o : facetClasses.keySet()) {
                        String facetClassString = (String) o;
                        fLogger.debug( "facet '" + facetClassString + "' : " + facetClasses.getLong( facetClassString ) );
                    }
                }
            }
        }
        if (allFacetClasses != null) {
            hitContainer.put( "FACETS", allFacetClasses );
        }
    }

    private void setDefaultInformations(IngridHits hitContainer, ResultSet resultSet, boolean ranked) {
        hitContainer.setPlugId( "ibus" );
        hitContainer.setInVolvedPlugs( resultSet.getPlugIdsWithResult().length );
        hitContainer.setRanked( ranked );
    }

    private ResultSet requestHits(IngridQuery query, int maxMilliseconds, PlugDescription[] plugsForQuery, int start, int requestLength) throws Exception {

        int plugsForQueryLength = plugsForQuery.length;
        boolean allowEmptyResults = query.isGetUnrankedIPlugsWithNoResults();
        ResultSet resultSet = new ResultSet( allowEmptyResults, plugsForQueryLength );
        PlugQueryRequest[] requests = new PlugQueryRequest[plugsForQueryLength];
        Future<?>[] requestFutures = new Future[plugsForQueryLength];

        // check whether query contains "metainfo"
        boolean queryHasMetainfo = query.containsField( QueryUtil.FIELDNAME_METAINFO );

        // orig query and cloned query (if necessary)
        IngridQuery origQuery = query;
        IngridQuery clonedQuery = null;

        try {
            for (int i = 0; i < plugsForQueryLength; i++) {
                PlugDescription plugDescription = plugsForQuery[i];
                IPlug plugProxy = this.fRegistry.getPlugProxy( plugDescription.getPlugId() );
                if (plugProxy != null) {

                    // check whether iplug can process "metainfo" and manipulate
                    // query accordingly.
                    if (queryHasMetainfo) {
                        if (!PlugDescriptionUtil.hasField( plugDescription, QueryUtil.FIELDNAME_METAINFO )) {
                            // iplug cannot process "metainfo". Remove
                            // "metainfo" from query.
                            // We have to do deep copy and remove to avoid
                            // conflicts (asynchronous call to iplugs !)
                            if (clonedQuery == null) {
                                clonedQuery = QueryUtil.deepCopy( query );
                                QueryUtil.removeFieldFromQuery( clonedQuery, QueryUtil.FIELDNAME_METAINFO );
                            }
                            query = clonedQuery;
                        } else {
                            // iplug can process "metainfo". Use original query
                            query = origQuery;
                        }
                    }

                    requests[i] = new PlugQueryRequest( plugProxy, this.fRegistry, plugDescription.getPlugId(), resultSet, query, start, requestLength );
                    requestFutures[i] = PooledThreadExecutor.submit( requests[i] );
                }
            }
            if (plugsForQueryLength > 0) {
                synchronized (resultSet) {
                    if (!resultSet.isComplete()) {
                        long startTimer = System.currentTimeMillis();
                        if (fLogger.isDebugEnabled()) {
                            fLogger.debug( "Resultset not complete yet. Wait for max " + maxMilliseconds + " ms." );
                        }
                        while (!resultSet.isComplete() && (startTimer + maxMilliseconds) > System.currentTimeMillis()) {
                            try {
                                resultSet.wait( 1000 );
                                if (fLogger.isDebugEnabled()) {
                                    if (!resultSet.isComplete() && (startTimer + maxMilliseconds) > System.currentTimeMillis()) {
                                        fLogger.debug( "Waiting for results thread [" + Thread.currentThread().getName() + "] finished  after " + (System.currentTimeMillis() - startTimer) + " ms. Resultset not complete. Wait another 1000 ms for resultset to complete." );
                                    }
                                }
                            } catch (InterruptedException e) {
                                if (fLogger.isWarnEnabled()) {
                                    fLogger.warn( "Waiting for results iterrupted.", e );
                                }
                                if (fLogger.isDebugEnabled()) {
                                    fLogger.debug( "Waiting for results thread [" + Thread.currentThread().getName() + "] iterrupted after " + (System.currentTimeMillis() - startTimer) + " ms." );
                                }
                            }
                        }
                        if (resultSet.isComplete()) {
                            if (fLogger.isDebugEnabled()) {
                                fLogger.debug( "Resultset complete within " + (System.currentTimeMillis() - startTimer) + " ms." );
                            }
                        } else {
                            fLogger.error( "Resultset incomplete, Timeout [" + maxMilliseconds + " ms] exceeded!" );
                            throw new TimeoutException( "Could not retrieve resultset in iBus within " + maxMilliseconds + " ms.");
                        }
                    }
                }
            }
        }
        // make sure the threads are canceled after
        finally {
            for (int i = 0; i < plugsForQueryLength; i++) {
                if (requestFutures[i] != null) {
                    if (fLogger.isDebugEnabled()) {
                        fLogger.debug( "Cancel future [" + requestFutures[i] + "]." );
                    }
                    requestFutures[i].cancel( true );
                }
                requests[i] = null; // for gc.
            }
            requests = null;
        }

        return resultSet;
    }

    private IngridHits orderResults(ResultSet resultSet, PlugDescription[] plugDescriptionsForQuery, IngridQuery query) {
        if (fLogger.isDebugEnabled()) {
            fLogger.debug( "order the results" );
        }

        // deliver also dummy hit for iPlugs with NO RESULTS !
        boolean addIPlugsWithNoResults = query.isGetUnrankedIPlugsWithNoResults();
        if (fLogger.isDebugEnabled()) {
            fLogger.debug( "orderResults: addIPlugsWithNoResults = " + addIPlugsWithNoResults );
            fLogger.debug( "orderResults: resultSet.size() = " + resultSet.size() );
        }

        int resultHitsCount = resultSet.size();
        int totalHits = 0;
        for (int i = 0; i < resultHitsCount; i++) {
            IngridHits hitContainer = (IngridHits) resultSet.get( i );
            int pos = getPlugPosition( plugDescriptionsForQuery, hitContainer.getPlugId() );
            hitContainer.putInt( Comparators.UNRANKED_HITS_COMPARATOR_POSITION, pos );
            totalHits += hitContainer.length();

            if (fLogger.isDebugEnabled()) {
                fLogger.debug( "orderResults: hitContainer, plugId = " + hitContainer.getPlugId() + ", pos = " + pos + ", hitContainer.length = " + hitContainer.length() );
            }

            if (hitContainer.length() <= 0 && addIPlugsWithNoResults) {
                // care for correct number. Dummy hit will be added if no
                // results !
                if (fLogger.isDebugEnabled()) {
                    fLogger.debug( "orderResults: add 1 to total num hits (dummy hit)" );
                }
                totalHits++;
            }
        }
        Collections.sort( resultSet, Comparators.UNRANKED_HITS_COMPARATOR );
        List orderedHits = new LinkedList();
        for (int i = 0; i < resultHitsCount; i++) {
            IngridHits hitContainer = (IngridHits) resultSet.get( i );
            IngridHit[] hits = hitContainer.getHits();
            if (hits != null && hits.length > 0) {
                orderedHits.addAll( Arrays.asList( hits ) );
            } else if (addIPlugsWithNoResults) {
                // add dummy hit !
                IngridHit dummyHit = new IngridHit( hitContainer.getPlugId(), "-1", -1, 0.0f );
                dummyHit.setDummyHit( true );
                orderedHits.add( dummyHit );
                if (fLogger.isDebugEnabled()) {
                    fLogger.debug( "orderResults: added dummy hit " + dummyHit );
                }
            }

        }
        IngridHits result = new IngridHits( totalHits, (IngridHit[]) orderedHits.toArray( new IngridHit[orderedHits.size()] ) );

        orderedHits.clear();
        orderedHits = null;

        return result;
    }

    private int getPlugPosition(PlugDescription[] plugDescriptionsForQuery, String plugId) {
        for (int i = 0; i < plugDescriptionsForQuery.length; i++) {
            if (plugDescriptionsForQuery[i].getPlugId().equals( plugId )) {
                return i;
            }
        }
        if (fLogger.isWarnEnabled()) {
            fLogger.warn( "plugId '" + plugId + "' not contained" );
        }
        return Integer.MAX_VALUE;
    }

    private IngridHits normalizeScores(List<IngridHits> resultSet, boolean skipNormalization) {
        if (fLogger.isDebugEnabled()) {
            fLogger.debug( "normalize the results" );
        }

        int totalHits = 0;
        int count = resultSet.size();
        List<IngridHit> documents = new LinkedList<IngridHit>();
        for (int i = 0; i < count; i++) {
            float maxScore = 1.0f;
            IngridHits hitContainer = resultSet.get( i );
            totalHits += hitContainer.length();
            if (hitContainer.getHits().length > 0) {
                Float boost = this.fRegistry.getGlobalRankingBoost( hitContainer.getPlugId() );
                IngridHit[] resultHits = hitContainer.getHits();
                if (null != boost) {
                    for (int j = 0; j < resultHits.length; j++) {
                        float score = 1.0f;
                        if (hitContainer.isRanked()) {
                            score = resultHits[j].getScore();
                            score = score * boost.floatValue();
                        }
                        resultHits[j].setScore( score );
                    }
                }

                // normalize scores of the results of this iPlug
                // so maxScore will never get bigger than 1 now!
                if (!skipNormalization && maxScore < resultHits[0].getScore()) {
                    normalizeHits( hitContainer, resultHits[0].getScore() );
                }
            }

            IngridHit[] toAddHits = hitContainer.getHits();
            if (toAddHits != null) {
                documents.addAll( Arrays.asList( toAddHits ) );
            }
        }

        IngridHits result = new IngridHits( totalHits, sortHits( (IngridHit[]) documents.toArray( new IngridHit[documents.size()] ) ) );

        // add timings for the corresponding iplugs
        HashMap<String, Long> timings = new HashMap<String, Long>();
        for (IngridHits hits : resultSet) {
            timings.putAll( hits.getSearchTimings() );
        }
        result.setSearchTimings( timings );

        documents.clear();
        documents = null;

        return result;
    }

    private void normalizeHits(IngridHits hits, float maxScore) {
        // normalize Score
        for (IngridHit hit : hits.getHits()) {
            hit.setScore( hit.getScore() / maxScore );
        }
    }

    private IngridHit[] sortHits(IngridHit[] documents) {
        Arrays.sort( documents, Comparators.SCORE_HIT_COMPARATOR );
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
        System.arraycopy( hits, startHit, cuttedHits, 0, newLength );
        return cuttedHits;
    }

    private IngridHit[] cutHitsRight(IngridHit[] hits, int currentPage, int hitsPerPage, int startHit) {
        int pageStart = Math.min( ((currentPage - 1) * hitsPerPage), hits.length );
        int resultLength = 0;
        if (hits.length <= pageStart) {
            final int preLastPage = hits.length / hitsPerPage;
            pageStart = Math.min( (preLastPage * hitsPerPage), hits.length );
        }
        resultLength = Math.min( hits.length - pageStart, hitsPerPage );
        if (hits.length == resultLength) {
            return hits;
        }
        IngridHit[] cuttedHits = new IngridHit[resultLength];
        System.arraycopy( hits, pageStart, cuttedHits, 0, resultLength );

        return cuttedHits;
    }

    public Record getRecord(IngridHit hit) throws Exception {
        if (fLogger.isDebugEnabled()) {
            fLogger.debug( "get record for: " + hit.getId() + " from iPlug : " + hit.getPlugId() + " started" );
        }

        PlugDescription plugDescription = getIPlugRegistry().getPlugDescription( hit.getPlugId() );

        if (plugDescription == null) {
            if (fLogger.isDebugEnabled()) {
                fLogger.debug("Using central index for getting record");
            }

            IPlug centralIndexPlugProxy = this.fRegistry.getPlugProxy(SearchService.CENTRAL_INDEX_ID);
            return ((IRecordLoader) centralIndexPlugProxy).getRecord( hit );

        } else {

            IPlug plugProxy = this.fRegistry.getPlugProxy( hit.getPlugId() );
            if (plugProxy == null) {
                throw new IllegalStateException( "plug '" + hit.getPlugId() + "' currently not available." );
            }

            if (plugDescription.isRecordloader()) {
                return ((IRecordLoader) plugProxy).getRecord( hit );
            }
            if (fLogger.isWarnEnabled()) {
                fLogger.warn( "plug does not implement record loader: " + plugDescription.getPlugId() + " but was requested to load a record" );
            }

        }

        return null;
    }

    public IngridHitDetail getDetail(IngridHit hit, IngridQuery ingridQuery, String[] requestedFields) {
        long startGetDetail = 0;
        if (fLogger.isDebugEnabled()) {
            startGetDetail = System.currentTimeMillis();
        }
        if (requestedFields == null) {
            requestedFields = new String[0];
        }
        IPlug plugProxy = this.fRegistry.getPlugProxy( hit.getPlugId() );
        try {
            long time = System.currentTimeMillis();
            if (fLogger.isDebugEnabled()) {
                fLogger.debug( "(search) detail start " + hit.getPlugId() + " " + ingridQuery.hashCode() );
            }
            IngridHitDetail detail = plugProxy.getDetail( hit, ingridQuery, requestedFields );
            if (fLogger.isDebugEnabled()) {
                fLogger.debug( "(search) detail end " + hit.getPlugId() + " " + ingridQuery.hashCode() + " within " + (System.currentTimeMillis() - time) + " ms." );
            }
            detail.put( IngridHitDetail.DETAIL_TIMING, (System.currentTimeMillis() - time) );
            pushMetaData( detail );
            if (fLogger.isDebugEnabled()) {
                fLogger.debug( "TIMING: Create detail for Query (" + ingridQuery.hashCode() + ") in " + (System.currentTimeMillis() - startGetDetail) + "ms." );
            }
            return detail;
        } catch (Exception e) {
            if (fLogger.isErrorEnabled()) {
                fLogger.error("Error getting detail", e);
            }
        }

        return null;
    }

    public IngridHitDetail[] getDetails(IngridHit[] hits, IngridQuery query, String[] requestedFields) throws Exception {
        long startGetDetails = 0;
        if (fLogger.isDebugEnabled()) {
            startGetDetails = System.currentTimeMillis();
        }
        if (requestedFields == null) {
            requestedFields = new String[0];
        }
        // collect requests for plugs
        HashMap hashMap = new HashMap();
        IngridHit hit = null;
        for (int i = 0; i < hits.length; i++) {
            hit = hits[i];
            // ignore hit if hit is "placeholder"
            if (hit.isDummyHit()) {
                if (fLogger.isDebugEnabled()) {
                    fLogger.debug( "getDetails: do NOT call iPlug for dummy hit: " + hit );
                }
                continue;
            }
            ArrayList requestHitList = (ArrayList) hashMap.get( hit.getPlugId() );
            if (requestHitList == null) {
                requestHitList = new ArrayList();
                hashMap.put( hit.getPlugId(), requestHitList );
            }
            requestHitList.add( hit );
        }
        // send requests and collect response
        Iterator iterator = hashMap.keySet().iterator();
        IPlug plugProxy;
        ArrayList resultList = new ArrayList( hits.length );
        Random random = new Random( System.currentTimeMillis() );
        long time = 0;
        while (iterator.hasNext()) {
            String plugId = (String) iterator.next();
            ArrayList requestHitList = (ArrayList) hashMap.get( plugId );
            if (requestHitList != null) {
                IngridHit[] requestHits = (IngridHit[]) requestHitList.toArray( new IngridHit[requestHitList.size()] );
                plugProxy = this.fRegistry.getPlugProxy( plugId );

                if (fLogger.isDebugEnabled()) {
                    fLogger.debug( "(search) details start " + plugId + " (" + requestHits.length + ") " + query.hashCode() );
                }
                time = System.currentTimeMillis();
                IngridHitDetail[] responseDetails = plugProxy.getDetails( requestHits, query, requestedFields );
                if (fLogger.isDebugEnabled()) {
                    fLogger.debug( "(search) details ends (" + responseDetails.length + ")" + plugId + " query:" + query.hashCode() + " within "
                            + (System.currentTimeMillis() - time) + " ms." );
                }
                for (int i = 0; i < responseDetails.length; i++) {
                    if (responseDetails[i] == null) {
                        if (fLogger.isErrorEnabled()) {
                            fLogger.error( plugId + ": responded details that are null (set a pseudo responseDetail" );
                        }
                        responseDetails[i] = new IngridHitDetail( plugId, String.valueOf(random.nextInt()), random.nextInt(), 0.0f, "", "" );
                    }
                    responseDetails[i].put( IngridHitDetail.DETAIL_TIMING, (System.currentTimeMillis() - time) );
                }

                resultList.addAll( Arrays.asList( responseDetails ) );
                // FIXME: to improve performance we can use an Array instead
                // of a list here.
            }

            if (null != requestHitList) {
                requestHitList.clear();
                requestHitList = null;
            }
        }

        hashMap.clear();
        hashMap = null;

        // int count = resultList.size();
        IngridHitDetail[] resultDetails = (IngridHitDetail[]) resultList.toArray( new IngridHitDetail[resultList.size()] );

        resultList.clear();
        resultList = null;

        // sort to be in the same order as the requested hits.
        IngridHitDetail[] details = new IngridHitDetail[hits.length];
        for (int i = 0; i < hits.length; i++) {
            // set dummy detail if hit is "placeholder"
            if (hits[i].isDummyHit()) {
                details[i] = new IngridHitDetail( hit, "dummy hit", "" );
                details[i].setDummyHit( true );
                if (fLogger.isDebugEnabled()) {
                    fLogger.debug( "getDetails: dummy hit, add dummy detail: " + details[i] );
                }
                continue;
            }

            String plugId = hits[i].getPlugId();
            String documentId = getDocIdAsString( hits[i] );

            boolean found = false;

            // get the details of the hits
            for (int j = 0; j < resultDetails.length; j++) {
                IngridHitDetail detail = resultDetails[j];
                String detailDocId = getDocIdAsString( detail );
                if (documentId.equals( detailDocId ) && detail.getPlugId().equals( plugId )) {
                    details[i] = detail;
                    pushMetaData( details[i] ); // push meta data to details
                    found = true;
                    break;
                }
            }
            if (!found) {
                if (fLogger.isErrorEnabled()) {
                    fLogger.error( "unable to find details getDetails: " + hit.toString() );
                }
                details[i] = new IngridHitDetail( hit, "no details found", "" );
            }
        }
        if (fLogger.isDebugEnabled()) {
            fLogger.debug( "TIMING: Create details for Query (" + query.hashCode() + ") in " + (System.currentTimeMillis() - startGetDetails) + "ms." );
        }
        return details;
    }

    /**
     * This function is used to handle the fallback to the old docId usage,
     * where it was an integer.
     * 
     * @return
     */
    private String getDocIdAsString(IngridHit hit) {
        String documentId = hit.getDocumentId();
        if (documentId == null || "null".equals( documentId )) {
            documentId = String.valueOf( hit.getInt( 0 ) );
        }
        return documentId;
    }

    private void pushMetaData(IngridHitDetail detail) {
        PlugDescription plugDescription;
        plugDescription = this.fRegistry.getPlugDescription( detail.getPlugId() );
        if (plugDescription != null) {
            detail.setIplugClassName( plugDescription.getIPlugClass() );
            if (detail.getOrganisation() == null) detail.setOrganisation( plugDescription.getOrganisation() );
            if (detail.getDataSourceName() == null) detail.setDataSourceName( plugDescription.getDataSourceName() );
        }

    }

    /**
     * A pipe with pre process and post process functionality for a query. Every
     * query goes through the posst process pipe before the search and the pre
     * process pipe after the search.
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
        return this.fRegistry.containsPlugDescription( plugId, md5Hash );
    }

    public void addPlugDescription(PlugDescription plugDescription) {
        if (null != plugDescription) {
            if (fLogger.isInfoEnabled()) {
                fLogger.info( "adding or updating plug '" + plugDescription.getPlugId() + "' current plug count:" + getAllIPlugs().length );
            }
            this.fRegistry.addPlugDescription( plugDescription );
        } else {
            if (fLogger.isErrorEnabled()) {
                fLogger.error( "Cannot add IPlug: plugdescription is null." );
            }
        }
    }

    public void removePlugDescription(PlugDescription plugDescription) {
        if (fLogger.isInfoEnabled()) {
            fLogger.info( "removing plug '" + plugDescription.getPlugId() + "' current plug count:" + getAllIPlugs().length );
        }
        this.fRegistry.removePlug( plugDescription.getPlugId() );
    }

    public PlugDescription[] getAllIPlugs() {
        return this.fRegistry.getAllIPlugs();
    }

    public PlugDescription[] getAllIPlugsWithoutTimeLimitation() {
        return this.fRegistry.getAllIPlugsWithoutTimeLimitation();
    }

    public PlugDescription getIPlug(String plugId) {
        PlugDescription plugDescription = this.fRegistry.getPlugDescription( plugId );
        if (plugDescription == null) {
            plugDescription = this.fRegistry.getPlugDescriptionFromIndex(plugId);
        } else {
            plugDescription = (PlugDescription) plugDescription.clone();
            plugDescription.remove( "overrideProxy" );
        }
        return plugDescription;
    }

    public void close() throws Exception {
        // nothing
    }

    @Override
    public Serializable getMetadata(String plugId, String metadataKey) {
        Metadata metadata = getMetadata( plugId );
        return metadata != null ? metadata.getMetadata( metadataKey ) : null;
    }

    @Override
    public Metadata getMetadata(String plugId) {
        PlugDescription plugDescription = getIPlug( plugId );
        return plugDescription != null ? plugDescription.getMetadata() : null;
    }

    @Override
    public Metadata getMetadata() {
        return _metadata;
    }

    public void setMetadata(Metadata metadata) {
        _metadata = metadata;
    }

    public IngridHits searchAndDetail(IngridQuery query, int hitsPerPage, int currentPage, int startHit, int maxMilliseconds, String[] requestedFields) throws Exception {
        if (debug.canDebugNow()) {
            // the query is used to identify the right Query during the analysis
            // where several threads are running
            debug.setQuery( query );
        }
        IngridHits searchedHits = search( query, hitsPerPage, currentPage, startHit, maxMilliseconds );
        IngridHit[] hits = searchedHits.getHits();
        IngridHitDetail[] details = getDetails( hits, query, requestedFields );
        for (int i = 0; i < hits.length; i++) {
            IngridHit ingridHit = hits[i];
            IngridHitDetail ingridHitDetail = details[i];
            ingridHit.setHitDetail( ingridHitDetail );
        }
        // make sure that the debugging is deactivated after each search
        if (debug.isActive( query )) {
            debug.addEvent( new DebugEvent( "Total Hits", "" + searchedHits.length() ) );
            List<String> list = new ArrayList<String>();
            for (IngridHit detail : details) {
                list.add( detail.getString( "title" ) + "(score: " + detail.getScore() + ", iPlug: " + detail.getPlugId() + ")" );
            }
            debug.addEvent( new DebugEvent( "Result", list ) );
            debug.setInactive();
        }
        return searchedHits;
    }

    public DebugQuery getDebugInfo() {
        return this.debug;
    }

    @Override
    public IngridDocument call(IngridCall targetInfo) throws Exception {

        IPlug plugProxy;
        if (SearchService.CENTRAL_INDEX_ID.equals(targetInfo.getTarget()) || ManagementService.MANAGEMENT_IPLUG_ID.equals(targetInfo.getTarget())) {
            plugProxy = this.fRegistry.getPlugProxy(targetInfo.getTarget());
        } else if ("iBus".equals(targetInfo.getTarget())) {
            return handleIBusCalls(targetInfo);
        } else {
            plugProxy = this.fRegistry.getRealPlugProxy(targetInfo.getTarget());
        }
        IngridDocument call;

        if (fLogger.isDebugEnabled()) {
            fLogger.debug("Custom iBus call: " + targetInfo.getMethod());
        }

        if (plugProxy != null) {
            call = plugProxy.call( targetInfo );
        } else {
            call = new IngridDocument();
            call.putBoolean( "success", false );
            call.put( "error", "iPlug not found: " + targetInfo.getTarget() );
        }
        return call;
    }

    private IngridDocument handleIBusCalls(IngridCall targetInfo) throws Exception {
        IngridDocument doc = null;
        Set<String> result = null;
        boolean success = false;
        switch (targetInfo.getMethod()) {
            case "activateIndex":
                success = this.settingsService.activateIndexType((String) targetInfo.getParameter());
                break;
            case "deactivateIndex":
                success = this.settingsService.deactivateIndexType((String) targetInfo.getParameter());
                break;
            case "getActiveIndices":
                result = this.settingsService.getActiveComponentIds();
                break;
            default:
                fLogger.warn( "The following method is not supported: " + targetInfo.getMethod() );
        }

        doc = new IngridDocument();
        doc.put("success", success);
        doc.put("result", result);
        return doc;
    }
}
