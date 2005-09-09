/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.net;

import java.util.Arrays;

import de.ingrid.ibus.ResultSet;
import de.ingrid.iplug.IIPlug;
import de.ingrid.utils.IDataSource;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.IngridQuery;

public class PlugQueryConnection extends Thread {

    private IPlugProxyFactory fFactory;

    private IIPlug fPlug;

    private IngridQuery fQuery;

    private int fLength;

    private ResultSet fResultSet;

    public PlugQueryConnection(IPlugProxyFactory proxyFactory, IIPlug plug, IngridQuery query, int length,
            ResultSet resultSet) {
        this.fFactory = proxyFactory;
        this.fPlug = plug;
        this.fQuery = query;
        this.fLength = length;
        this.fResultSet = resultSet;
    }

    public void run() {
        try {
            IDataSource dataSource = this.fFactory.createDataSourceProxy(this.fPlug);
            IngridDocument[] documents = dataSource.search(this.fQuery, this.fLength);
            this.fResultSet.addAll(Arrays.asList(documents));
            this.fResultSet.resultsAdded();

        } catch (Exception e) {
            e.printStackTrace();
            // TODO: log exception
        }
    }
}
