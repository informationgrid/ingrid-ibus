/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.net;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.ibus.ResultSet;
import de.ingrid.iplug.IPlug;
import de.ingrid.iplug.PlugDescription;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;

/**
 * A thread for one query to one IPlug. Created on 24.10.2005
 */
public class PlugQueryConnection extends Thread {

    private final static Log fLog = LogFactory.getLog(PlugQueryConnection.class);

    private IPlugProxyFactory fFactory;

    private PlugDescription fPlugDescription;

    private IngridQuery fQuery;

    private int fLength;

    private ResultSet fResultSet = null;

    private int fStart;

    private IPlug fIPlug = null;

    private boolean fStopped = false;

    /**
     * @param proxyFactory
     * @param plugDescription
     * @param query
     * @param start
     * @param length
     * @throws Exception
     */
    public PlugQueryConnection(IPlugProxyFactory proxyFactory, PlugDescription plugDescription, IngridQuery query,
            int start, int length) throws Exception {
        this.fFactory = proxyFactory;
        this.fPlugDescription = plugDescription;
        this.fQuery = query;
        this.fStart = start;
        this.fLength = length;
        this.fIPlug = this.fFactory.createPlugProxy(this.fPlugDescription);
    }

    /**
     * @see java.lang.Thread#run()
     */
    public void run() {
        synchronized (this) {
            final String iPlugId = this.fPlugDescription.getPlugId();

            while (!this.fStopped) {
                try {
                    fLog.debug("Search in IPlug " + iPlugId + " ...");
                    IngridHits hits = this.fIPlug.search(this.fQuery, this.fStart, this.fLength);
                    if (null != this.fResultSet) {
                        fLog.debug("Set result");
                        this.fResultSet.add(hits);
                    } else {
                        fLog.error("No ResultSet set where IPlug " + iPlugId + " could add its results.");
                    }
                } catch (Exception e) {
                    fLog.error("Could not retrieve query result from IPlug: " + iPlugId, e);
                } finally {
                    if (null != this.fResultSet) {
                        this.fResultSet.resultsAdded();
                    } else {
                        fLog.error("No ResultSet set where IPlug " + iPlugId + " can sent its completion.");
                    }
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        fLog.debug("Wakeup thread for IPlug: " + iPlugId);
                    }
                }
            }
        }
    }

    /**
     * @param resultSet
     */
    public void setResultSet(ResultSet resultSet) {
        this.fResultSet = resultSet;
    }

    /**
     * @param query
     */
    public void setQuery(IngridQuery query) {
        this.fQuery = query;
    }

    /**
     * @param start
     */
    public void setStart(int start) {
        this.fStart = start;
    }

    /**
     * @param length
     */
    public void setLength(int length) {
        this.fLength = length;
    }

    /**
     * @param stop
     */
    public void setStop(final boolean stop) {
        this.fStopped = stop;
    }
}
