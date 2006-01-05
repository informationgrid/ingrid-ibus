/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import de.ingrid.iplug.IPlug;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;

public class DummyIPlug implements IPlug {

    public IngridHits search(IngridQuery query, int start, int lenght) {
        return new IngridHits("bla", 1, new IngridHit[] { new IngridHit("provider", 23, 0.23f) });
    }

}
