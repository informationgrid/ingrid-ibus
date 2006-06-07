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

    /**
     * @throws Exception 
     * @see de.ingrid.ibus.net.IPlugProxyFactory#createPlugProxy(de.ingrid.utils.PlugDescription)
     */
    public IPlug createPlugProxy(PlugDescription plugDescription, String busurl) throws Exception {
        IPlug plug=new DummyIPlug(plugDescription.getPlugId());
        plug.configure(plugDescription);
        
        return plug;
    }

}
