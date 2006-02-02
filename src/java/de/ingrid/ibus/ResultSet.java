/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 
 */
public class ResultSet extends ArrayList {

    private int fNumberOfConnections;

    private int fNumberOfFinsihedConnections = 0;

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
            synchronized (this) {
                this.notify();
            }
        }
    }

    /**
     * @see java.util.ArrayList#addAll(java.util.Collection)
     */
    public synchronized boolean addAll(Collection c) {
        return super.addAll(c);
    }
}
