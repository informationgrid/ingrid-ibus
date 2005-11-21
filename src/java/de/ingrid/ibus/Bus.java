/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import de.ingrid.ibus.net.IPlugProxyFactory;
import de.ingrid.ibus.net.PlugQueryConnection;
import de.ingrid.ibus.registry.Registry;
import de.ingrid.ibus.registry.SyntaxInterpreter;
import de.ingrid.iplug.PlugDescription;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.processor.ProcessorPipe;
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
    private Registry fRegistry = new Registry(100000);

    private ProcessorPipe fProcessorPipe = new ProcessorPipe();

    private IPlugProxyFactory fProxyFactory = null;

    private static Bus fBusInstance = null;

    /**
     * @param factory
     */
    public Bus(IPlugProxyFactory factory) {
        Bus.fBusInstance = this;
        this.fProxyFactory = factory;
    }

    /**
     * multicast the query to all connected IPlugs and return founded results
     * 
     * @param query
     * @param hitsPerPage
     * @param currentPage
     * @param length
     * @param maxMilliseconds
     * @return array of founded documents
     * @throws Exception
     */
    public IngridDocument[] search(IngridQuery query, int hitsPerPage, int currentPage, int length, int maxMilliseconds)
            throws Exception {
        this.fProcessorPipe.preProcess(query);
        // TODO add grouping
        PlugDescription[] plugsForQuery = SyntaxInterpreter.getIPlugsForQuery(query, this.fRegistry);
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

        IngridDocument[] results = (IngridDocument[]) resultSet.toArray(new IngridDocument[resultSet.size()]);
        this.fProcessorPipe.postProcess(query, results);
        return results;

    }
    
    /**
     * @param factory
     * @return The current Bus instance.
     */
    public static Bus getInstance(IPlugProxyFactory factory) {
        if (null == fBusInstance) {
            new Bus(factory);
        }
        
        return fBusInstance;
    }

    /**
     * @return the iplug regestry
     */
    public Registry getIPlugRegistry() {
        return this.fRegistry;
    }

    /**
     * @return the processing pipe
     */
    public ProcessorPipe getProccessorPipe() {
        return this.fProcessorPipe;
    }

}
