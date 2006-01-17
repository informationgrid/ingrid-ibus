/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.ibus.net.IPlugProxyFactory;
import de.ingrid.ibus.net.PlugQueryConnection;
import de.ingrid.ibus.registry.Registry;
import de.ingrid.ibus.registry.SyntaxInterpreter;
import de.ingrid.iplug.PlugDescription;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.processor.ProcessorPipe;
import de.ingrid.utils.query.IngridQuery;

/**
 * The IBus a centralized Bus that routes queries and return results
 * 
 * created on 09.08.2005
 * 
 * @author sg
 * @version $Revision: 1.3 $
 */
public class Bus implements IBus {

    private static Log fLogger = LogFactory.getLog(Bus.class);

    // TODO INGRID-398 we need to made the lifetime configurable.
    private Registry fRegistry = new Registry(100000);

    private ProcessorPipe fProcessorPipe = new ProcessorPipe();

    private IPlugProxyFactory fProxyFactory = null;

    private static Bus fBusInstance = null;

    /**
     * 
     */
    public Bus() {
        // for deserialization
    }

    /**
     * @param factory
     */
    public Bus(IPlugProxyFactory factory) {
        Bus.fBusInstance = this;
        this.fProxyFactory = factory;
    }

    /**
     * multicast the query to all connected IPlugs and return founded results
     * 
     * @param query
     * @param hitsPerPage
     * @param currentPage
     * @param length
     * @param maxMilliseconds
     * @return IngridHits as container for hits and meta data.
     * @throws Exception
     */
    public IngridHits search(IngridQuery query, int hitsPerPage, int currentPage, int length, int maxMilliseconds)
            throws Exception {
        this.fProcessorPipe.preProcess(query);
        // TODO add grouping
        PlugDescription[] plugsForQuery = SyntaxInterpreter.getIPlugsForQuery(query, this.fRegistry);
        PlugQueryConnection[] connections = new PlugQueryConnection[plugsForQuery.length];
        ResultSet resultSet = new ResultSet(connections.length);
        for (int i = 0; i < plugsForQuery.length; i++) {
            PlugQueryConnection connection = new PlugQueryConnection(this.fProxyFactory, plugsForQuery[i], query,
                    length, i, resultSet);
            connection.start();
        }
        long end = System.currentTimeMillis() + maxMilliseconds;
        while (end > System.currentTimeMillis() && !resultSet.isComplete()) {
            // System.out.println("waiting");
            Thread.sleep(10);
        }
        int totalHits = 0;
        int count = resultSet.size();
        ArrayList documents = new ArrayList();
        for (int i = 0; i < count; i++) {
            IngridHits hits = (IngridHits) resultSet.get(i);
            totalHits += hits.length();
            documents.addAll(Arrays.asList(hits.getHits()));
        }

        IngridHit[] hits = getSortedAndLimitedHits((IngridHit[]) documents.toArray(new IngridHit[documents.size()]),
                hitsPerPage, currentPage, length);

        this.fProcessorPipe.postProcess(query, hits);

        return new IngridHits("ibus", totalHits, hits);
    }

    /**
     * @param query
     * @param hitsPerPage
     * @param currentPage
     * @param length
     * @param maxMilliseconds
     * @return IngridHits as container for hits and meta data.
     * @throws Exception
     */
    public static IngridHits searchR(IngridQuery query, int hitsPerPage, int currentPage, int length,
            int maxMilliseconds) throws Exception {
        IngridHits result = null;

        if (null != fBusInstance) {
            result = fBusInstance.search(query, hitsPerPage, currentPage, length, maxMilliseconds);
        } else {
            fLogger.error("Bus not yet instantiated.");
        }

        return result;
    }

    private IngridHit[] getSortedAndLimitedHits(IngridHit[] documents, int hitsPerPage, int currentPage, int length) {
        Arrays.sort(documents, new IngridHitComparator());
        length = Math.min(documents.length, length);
        IngridHit[] hits = new IngridHit[length];
        System.arraycopy(documents, 0, hits, 0, length);
        return hits;
    }

    /**
     * Returns the current IBus instance.
     * 
     * @return The IBus instance.
     */
    public static Bus getInstance() {
        Bus result = null;
        if (null != fBusInstance) {
            result = fBusInstance;
        } else {
            fLogger.error("Bus not yet instantiated.");
        }

        return result;
    }

    /**
     * @param plugDescription
     */
    public static void addIPlug(PlugDescription plugDescription) {
        if (null != fBusInstance) {
            fBusInstance.fRegistry.addIPlug(plugDescription);
        } else {
            fLogger.error("Bus not yet instantiated.");
        }
    }

    /**
     * @return the iplug regestry
     */
    public Registry getIPlugRegistry() {
        return this.fRegistry;
    }

    /**
     * @return the processing pipe
     */
    public ProcessorPipe getProccessorPipe() {
        return this.fProcessorPipe;
    }
}
