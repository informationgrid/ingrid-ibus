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
import de.ingrid.utils.query.IngridQuery;

/**
 * A thread for one query to one IPlug.
 * 
 * <p/>created on 24.10.2005
 * 
 * @version $Revision: $
 * @author jz
 * @author $Author: ${lastedit}
 *  
 */
public class PlugQueryConnection extends Thread {

    private final static Log fLog = LogFactory.getLog(PlugQueryConnection.class);

    private IPlugProxyFactory fFactory;

    private PlugDescription fPlugDescription;

    private IngridQuery fQuery;

    private int fLength;

    private ResultSet fResultSet;

    private int fStart;

    /**
     * @param proxyFactory
     * @param plugDescription
     * @param query
     * @param start
     * @param length
     * @param resultSet
     */
    public PlugQueryConnection(IPlugProxyFactory proxyFactory, PlugDescription plugDescription, IngridQuery query,
            int start, int length, ResultSet resultSet) {
        this.fFactory = proxyFactory;
        this.fPlugDescription = plugDescription;
        this.fQuery = query;
        this.fStart = start;
        this.fLength = length;
        this.fResultSet = resultSet;
    }

    /**
     * @see java.lang.Thread#run()
     */
    public void run() {
        try {
            IPlug plug = this.fFactory.createPlugProxy(this.fPlugDescription);
            this.fResultSet.add(plug.search(this.fQuery, this.fStart, this.fLength));
            this.fResultSet.resultsAdded();
        } catch (Exception e) {
            this.fLog.error("could not retrieve query result from iplug " + this.fPlugDescription.getPlugId(), e);
        }
    }
}
