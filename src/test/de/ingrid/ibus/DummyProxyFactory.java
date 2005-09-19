/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import de.ingrid.ibus.net.IPlugProxyFactory;
import de.ingrid.iplug.IPlug;
import de.ingrid.iplug.PlugDescription;

public class DummyProxyFactory implements IPlugProxyFactory {

    public IPlug createPlugProxy(PlugDescription plug) {
        return new DummyIPlug();
    }

}
