/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import java.util.ArrayList;
import java.util.Arrays;

import de.ingrid.ibus.registry.Regestry;
import de.ingrid.iplug.IIPlug;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.IngridQuery;

/**
 * The IBus a centralized Bus that routes queries and return results
 * 
 * created on 09.08.2005
 * 
 * @author sg
 * @version $Revision: 1.3 $
 */
public class Bus {

    private Regestry fRegestry = new Regestry();

    /**
     * multicast the query to all connected IPlugs and return founded results
     * 
     * @param query
     * @param start
     * @param length
     * @return array of founded documents
     * @throws Exception
     */
    public IngridDocument[] search(IngridQuery query, int start, int length, int maxMilliseconds) throws Exception {
        IIPlug[] plugsForQuery = this.fRegestry.getIPlugsForQuery(query);
        PlugQueryConnection[] connections = new PlugQueryConnection[plugsForQuery.length];
        ResultSet resultSet = new ResultSet(connections.length, this);
        for (int i = 0; i < plugsForQuery.length; i++) {
            PlugQueryConnection connection = new PlugQueryConnection(plugsForQuery[i], query, resultSet);
            connection.start();
        }
        long end = System.currentTimeMillis() + maxMilliseconds;
        while (end > System.currentTimeMillis() && !resultSet.isComplete()) {
//             System.out.println("waiting");
            Thread.sleep(10);
        }

        return (IngridDocument[]) resultSet.toArray(new IngridDocument[resultSet.size()]);

    }

    /**
     * @return the iplug regestry
     */
    public Regestry getIPlugRegestry() {
        return this.fRegestry;

    }

}
