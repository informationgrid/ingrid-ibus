/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import java.util.ArrayList;

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
    public boolean isComplete() {
        return this.fNumberOfConnections == this.fNumberOfFinsihedConnections;
    }

    /**
     *  
     */
    public void resultsAdded() {
        this.fNumberOfFinsihedConnections += 1;
    }
}
