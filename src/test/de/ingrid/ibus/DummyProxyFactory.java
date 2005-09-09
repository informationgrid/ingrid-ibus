/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import de.ingrid.ibus.net.IPlugProxyFactory;
import de.ingrid.iplug.IIPlug;
import de.ingrid.utils.IDataSource;

public class DummyProxyFactory implements IPlugProxyFactory {

    public IDataSource createDataSourceProxy(IIPlug plug) {
        // TODO Auto-generated method stub
        return new DummyDataSource();
    }

}
