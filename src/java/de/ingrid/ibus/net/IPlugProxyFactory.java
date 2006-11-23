/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.net;

import de.ingrid.utils.IPlug;
import de.ingrid.utils.PlugDescription;

/**
 * For implementing a factory for creation of IPlug proxies with the bus.
 */
public interface IPlugProxyFactory {

    /**
     * Creates a IPlug proxy from its description.
     * @param plug The descrption of a plug.
     * @param busurl The url the plug is connected to.
     * @return The created IPlug proxy instance.
     * @throws Exception If the plug cannot be created.
     */
    public IPlug createPlugProxy(PlugDescription plug, String busurl) throws Exception;

}
