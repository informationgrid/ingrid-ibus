/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.net;

import de.ingrid.utils.IPlug;
import de.ingrid.utils.PlugDescription;

/**
 * 
 */
public interface IPlugProxyFactory {

    /**
     * @param plug
     * @return The created IPlug instance.
     * @throws Exception
     */
    public IPlug createPlugProxy(PlugDescription plug) throws Exception;

}
