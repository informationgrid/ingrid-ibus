/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.ingrid.utils.IngridHits;

/**
 * 
 */
public class ResultSet extends ArrayList {

    private static final long serialVersionUID = ResultSet.class.getName().hashCode();

    private int fNumberOfConnections;

    private int fNumberOfFinsihedConnections;

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
     * @param hits
     * @return true
     */
    public synchronized boolean add(IngridHits hits) {
        if (hits == null) {
            throw new IllegalArgumentException("null can not added as Hits");
        }
        if (hits.getHits() == null || hits.getHits().length == 0) {
            return false;
        }
        return super.add(hits);
    }

    /**
     * @return all plugIds from the plugs which delivers a result.
     */
    public String[] getPlugIdsWithResult() {
        List plugIds = new ArrayList(size());
        for (Iterator iter = this.iterator(); iter.hasNext();) {
            IngridHits hits = (IngridHits) iter.next();
            plugIds.add(hits.getPlugId());
        }
        return (String[]) plugIds.toArray(new String[plugIds.size()]);
    }
}
