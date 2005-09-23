/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import de.ingrid.ibus.net.IPlugProxyFactory;
import de.ingrid.ibus.net.PlugQueryConnection;
import de.ingrid.ibus.registry.Regestry;
import de.ingrid.ibus.registry.SyntaxInterpreter;
import de.ingrid.iplug.PlugDescription;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.query.IngridQuery;

/**
 * The IBus a centralized Bus that routes queries and return results
 * 
 * created on 09.08.2005
 * 
 * @author sg
 * @version $Revision: 1.3 $
 */
public class Bus implements IBus {
    // TODO INGRID-398 we need to made the lifetime configurable.
    private Regestry fRegestry = new Regestry(100000);

    private IPlugProxyFactory fProxyFactory;

    public Bus(IPlugProxyFactory factory) {
        this.fProxyFactory = factory;
    }

    /**
     * multicast the query to all connected IPlugs and return founded results
     * 
     * @param query
     * @param start
     * @param length
     * @return array of founded documents
     * @throws Exception
     */
    public IngridDocument[] search(IngridQuery query, int hitsPerPage, int currentPage, int length, int maxMilliseconds)
            throws Exception {
        // TODO add grouping
        PlugDescription[] plugsForQuery = SyntaxInterpreter.getIPlugsForQuery(query, this.fRegestry);
        PlugQueryConnection[] connections = new PlugQueryConnection[plugsForQuery.length];
        ResultSet resultSet = new ResultSet(connections.length);
        for (int i = 0; i < plugsForQuery.length; i++) {
            PlugQueryConnection connection = new PlugQueryConnection(this.fProxyFactory, plugsForQuery[i], query,
                    length, i, resultSet);
            connection.start();
        }
        long end = System.currentTimeMillis() + maxMilliseconds;
        while (end > System.currentTimeMillis() && !resultSet.isComplete()) {
            // System.out.println("waiting");
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
