/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class ResultSet extends ArrayList {

    private static final long serialVersionUID = ResultSet.class.getName().hashCode();

    private int fNumberOfConnections;

    private int fNumberOfFinsihedConnections = 0;

    private List fPlugIdsWithResult;

    /**
     * @param numberOfConnections
     */
    public ResultSet(int numberOfConnections) {
        this.fNumberOfConnections = numberOfConnections;
    }

    /**
     * @return if all connections are finished
     */
    public synchronized boolean isComplete() {
        return this.fNumberOfConnections == this.fNumberOfFinsihedConnections;
    }

    /**
     * 
     */
    public synchronized void resultsAdded() {
        this.fNumberOfFinsihedConnections += 1;
        if (isComplete()) {
            this.notify();
        }
    }

    /**
     * @param arg0
     * @param plugId
     * @return true
     */
    public synchronized boolean add(Object arg0, String plugId) {
        if (arg0 == null) {
            throw new IllegalArgumentException("null can not added as Hits");
        }
        this.fPlugIdsWithResult.add(plugId);
        return super.add(arg0);
    }

    /**
     * @return all plugIds from the plugs which delivers a result.
     */
    public String[] getPlugIdsWithResult() {
        return (String[]) this.fPlugIdsWithResult.toArray(new String[this.fPlugIdsWithResult.size()]);
    }
}
