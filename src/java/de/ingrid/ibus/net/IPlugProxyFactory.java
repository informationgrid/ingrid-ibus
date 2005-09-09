/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.net;

import de.ingrid.iplug.IIPlug;
import de.ingrid.utils.IDataSource;

public interface IPlugProxyFactory {

    public IDataSource createDataSourceProxy(IIPlug plug);

}
