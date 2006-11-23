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
     * Stores the result of different processes. Has functionality to show if all processes added their result.
     * 
     * @param numberOfConnections
     *            The number of results that ResultSet should be contain.
     */
    public ResultSet(int numberOfConnections) {
        this.fNumberOfConnections = numberOfConnections;
    }

    /**
     * Checks if all results are added.
     * 
     * @return True if all results are finished and false if not.
     */
    public synchronized boolean isComplete() {
        return this.fNumberOfConnections == this.fNumberOfFinsihedConnections;
    }

    /**
     * Should be called if a result is added. If all results are added it notifies waiting threads.
     */
    public synchronized void resultsAdded() {
        this.fNumberOfFinsihedConnections += 1;
        if (isComplete()) {
            this.notify();
        }
    }

    /**
     * This method is for adding a IngridHits object from a search run.
     * 
     * @param hits
     *            The hits to be added. This cannot be null.
     * @return True if a result is added otherwise false.
     */
    public synchronized boolean add(IngridHits hits) {
        if (hits == null) {
            throw new IllegalArgumentException("Null can not be added as hits.");
        }
        if (hits.getHits() == null || hits.getHits().length == 0) {
            return false;
        }
        return super.add(hits);
    }

    /**
     * Is for getting a list of all plugs that should add hits to the ResultSet.
     * 
     * @return All plug ids from the plugs which delivers a result.
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
