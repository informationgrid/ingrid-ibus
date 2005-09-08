/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import java.util.ArrayList;

public class ResultSet extends ArrayList {

    private int fNumberOfConnections;
    private int fNumberOfFinsihedConnections = 0;
    private Bus fBus;

    public ResultSet(int numberOfConnections, Bus bus) {
        this.fNumberOfConnections = numberOfConnections;
        fBus =bus;
    }

    
    
    /**
     * @return if all connections are finished
     */
    public boolean isComplete() {
        return this.fNumberOfConnections == this.fNumberOfFinsihedConnections;
    }

}
