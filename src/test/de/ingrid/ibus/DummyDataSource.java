/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import de.ingrid.utils.IDataSource;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.IngridQuery;

public class DummyDataSource implements IDataSource {

    public IngridDocument[] search(IngridQuery query, int length) throws Exception {
        return new IngridDocument[] { new IngridDocument(new Long(System.currentTimeMillis()), " a result") };
    }

}
