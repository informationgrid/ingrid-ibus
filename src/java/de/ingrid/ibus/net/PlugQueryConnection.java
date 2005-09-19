/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.net;

import java.util.Arrays;

import de.ingrid.ibus.ResultSet;
import de.ingrid.iplug.IPlug;
import de.ingrid.iplug.PlugDescription;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.query.IngridQuery;

public class PlugQueryConnection extends Thread {

    private IPlugProxyFactory fFactory;

    private PlugDescription fPlug;

    private IngridQuery fQuery;

    private int fLength;

    private ResultSet fResultSet;

    private int fStart;

    public PlugQueryConnection(IPlugProxyFactory proxyFactory, PlugDescription plug, IngridQuery query, int start,
            int length, ResultSet resultSet) {
        this.fFactory = proxyFactory;
        this.fPlug = plug;
        this.fQuery = query;
        this.fStart = start;
        this.fLength = length;
        this.fResultSet = resultSet;
    }

    public void run() {
        try {
            IPlug plug = this.fFactory.createPlugProxy(this.fPlug);
            IngridDocument[] documents = plug.search(this.fQuery, this.fStart, this.fLength);
            this.fResultSet.addAll(Arrays.asList(documents));
            this.fResultSet.resultsAdded();

        } catch (Exception e) {
            e.printStackTrace();
            // TODO: log exception
        }
    }
}
