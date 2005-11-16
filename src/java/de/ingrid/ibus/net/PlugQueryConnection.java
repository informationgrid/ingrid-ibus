/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.net;

import java.util.Arrays;

import org.apache.log4j.Logger;

import de.ingrid.ibus.ResultSet;
import de.ingrid.iplug.IPlug;
import de.ingrid.iplug.PlugDescription;
import de.ingrid.utils.IngridDocument;
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

    private Logger fLog = Logger.getLogger(this.getClass());

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

    public void run() {
        try {
            IPlug plug = this.fFactory.createPlugProxy(this.fPlugDescription);
            IngridDocument[] documents = plug.search(this.fQuery, this.fStart, this.fLength);
            this.fResultSet.addAll(Arrays.asList(documents));
            this.fResultSet.resultsAdded();
        } catch (Exception e) {
            this.fLog.error("could not retrieve query result from iplug " + this.fPlugDescription.getPlugId(), e);
        }
    }
}
