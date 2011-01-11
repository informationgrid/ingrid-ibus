/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import de.ingrid.ibus.net.IPlugProxyFactory;
import de.ingrid.utils.IPlug;
import de.ingrid.utils.PlugDescription;

/**
 * 
 */
public class DummyProxyFactory implements IPlugProxyFactory {
    private float[][] useScoresPerIPlug = null;
    
    private int numCreatedIPlugs = 0;
    
    public DummyProxyFactory() {}
    
    public DummyProxyFactory(float[][] scores) {
        this.useScoresPerIPlug = scores;
    }
    
    /**
     * @throws Exception 
     * @see de.ingrid.ibus.net.IPlugProxyFactory#createPlugProxy(de.ingrid.utils.PlugDescription, java.lang.String)
     */
    public IPlug createPlugProxy(PlugDescription plugDescription, String busurl) throws Exception {
        IPlug plug;
        if (useScoresPerIPlug != null)
            plug = new DummyIPlug(plugDescription.getPlugId(), useScoresPerIPlug[numCreatedIPlugs++]);
        else
            plug = new DummyIPlug(plugDescription.getPlugId());
        plug.configure(plugDescription);
        
        return plug;
    }

}
