/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import de.ingrid.iplug.IPlug;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.query.IngridQuery;

public class DummyIPlug implements IPlug {


    public IngridDocument[] search(IngridQuery query, int start, int lenght) {
        return new IngridDocument[] { new IngridDocument(new Long(System.currentTimeMillis()), " a result") };
    }

}
