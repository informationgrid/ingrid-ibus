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
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;

/**
 * A thread for one query to one IPlug. Created on 24.10.2005
 */
public class PlugQueryRequest extends Thread {

    private final static Log fLog = LogFactory.getLog(PlugQueryRequest.class);

    private IngridQuery fQuery;

    private int fLength;

    private ResultSet fResultSet = null;

    private int fStart;

    private IPlug fIPlug = null;

    private boolean fStopped = false;

    private String fPlugId;

    /**
     * @param proxyFactory
     * @param plugDescription
     * @param query
     * @param start
     * @param length
     * @param plug 
     * @throws Exception
     */
    public PlugQueryRequest( IPlug plug, String plugId, ResultSet resultSet,  IngridQuery query, int start, int length) throws Exception {
        this.fIPlug = plug;
        this.fPlugId = plugId;
        this.fResultSet =  resultSet;
        this.fQuery = query;
        this.fStart = start;
        this.fLength = length;
        
    }

    /**
     * @see java.lang.Thread#run()
     */
    public void run() {
        synchronized (this) {

//            while (!this.fStopped) {
                try {
                    fLog.debug("Search in IPlug " + this.fPlugId + " ...");
                    IngridHits hits = this.fIPlug.search(this.fQuery, this.fStart, this.fLength);
                    if (null != this.fResultSet) {
                        fLog.debug("Set result");
                        this.fResultSet.add(hits);
                    } else {
                        fLog.error("No ResultSet set where IPlug " + this.fPlugId + " could add its results.");
                    }
                } catch (Exception e) {
                    fLog.error("Could not retrieve query result from IPlug: " + this.fPlugId, e);
                } finally {
                    if (null != this.fResultSet) {
                        this.fResultSet.resultsAdded();
                    } else {
                        fLog.error("No ResultSet set where IPlug " + this.fPlugId + " can sent its completion.");
                    }
//                    try {
//                        this.wait();
//                    } catch (InterruptedException e) {
//                        this.fStopped = true;
//                        fLog.debug("Wakeup thread for IPlug: " + this.fPlugId);
//                    }
                }
//            }
        }
    }

//    /**
//     * @param resultSet
//     */
//    public void setResultSet(ResultSet resultSet) {
//        this.fResultSet = resultSet;
//    }

//    /**
//     * @param query
//     */
//    public void setQuery(IngridQuery query) {
//        this.fQuery = query;
//    }

//    /**
//     * @param start
//     */
//    public void setStart(int start) {
//        this.fStart = start;
//    }

//    /**
//     * @param length
//     */
//    public void setLength(int length) {
//        this.fLength = length;
//    }
    
    /**
     * @return The IPlug itself.
     */
    public IPlug getIPlug() {
        return this.fIPlug;
    }
}
