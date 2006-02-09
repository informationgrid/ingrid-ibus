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

    private static final long serialVersionUID = ResultSet.class.getName().hashCode();

    private int fNumberOfConnections;

    private int fNumberOfFinsihedConnections = 0;

    private Object fMonitor;

    /**
     * @param numberOfConnections
     * @param monitor
     */
    public ResultSet(int numberOfConnections, Object monitor) {
        this.fNumberOfConnections = numberOfConnections;
        this.fMonitor = monitor;
    }

    /**
     * @return if all connections are finished
     */
    public boolean isComplete() {
        return this.fNumberOfConnections == this.fNumberOfFinsihedConnections;
    }

    /**
     * 
     */
    public void resultsAdded() {
        this.fNumberOfFinsihedConnections += 1;
        synchronized (fMonitor) {
            if (isComplete()) {
                fMonitor.notify();
            }
        }
    }

    /**
     * @see java.util.ArrayList#addAll(java.util.Collection)
     */
    public boolean addAll(Collection c) {
        return super.addAll(c);
    }
}
