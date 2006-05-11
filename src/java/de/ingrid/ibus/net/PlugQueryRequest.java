/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.net;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.ibus.ResultSet;
import de.ingrid.ibus.registry.Registry;
import de.ingrid.utils.IPlug;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;

/**
 * A thread for one query to one IPlug. Created on 24.10.2005
 */
public class PlugQueryRequest extends Thread {

    private final static Log fLog = LogFactory.getLog(PlugQueryRequest.class);

    private IngridQuery fQuery;

    private int fLength;

    private ResultSet fResultSet;

    private int fStart;

    private IPlug fIPlug;

    private String fPlugId;

    private Registry fRegestry;

    /**
     * @param query
     * @param start
     * @param length
     * @param plug
     * @param registry
     * @param plugId
     * @param resultSet
     * @throws Exception
     */
    public PlugQueryRequest(IPlug plug, Registry registry, String plugId, ResultSet resultSet, IngridQuery query,
            int start, int length) throws Exception {
        this.fIPlug = plug;
        this.fRegestry = registry;
        this.fPlugId = plugId;
        this.fResultSet = resultSet;
        this.fQuery = query;
        this.fStart = start;
        this.fLength = length;

    }

    /**
     * @see java.lang.Thread#run()
     */
    public void run() {
        long time = 0;
        try {
            if (fLog.isDebugEnabled()) {
                fLog.debug("Search in IPlug " + this.fPlugId + " ...");
                time = System.currentTimeMillis();
            }
            IngridHits hits = this.fIPlug.search(this.fQuery, this.fStart, this.fLength);
            hits.setPlugId(this.fPlugId);
            if (fLog.isDebugEnabled()) {
                fLog.debug("adding results from: " + this.fPlugId + " size: " + hits.length() + " time: "
                        + (System.currentTimeMillis() - time) + " ms");
            }
            this.fResultSet.add(hits, this.fPlugId);
        } catch (Exception e) {
            fLog.error("(REMOVING IPLUG!) Could not retrieve query result from IPlug: " + this.fPlugId, e);
            this.fRegestry.removePlugFromCache(this.fPlugId);
        } finally {
            if (this.fResultSet != null) {
                synchronized (this.fResultSet) {
                    this.fResultSet.resultsAdded();
                }
            } else {
                fLog.error("No ResultSet set where IPlug " + this.fPlugId + " can sent its completion.");
            }
        }
    }

    /**
     * @return The IPlug itself.
     */
    public IPlug getIPlug() {
        return this.fIPlug;
    }
}
