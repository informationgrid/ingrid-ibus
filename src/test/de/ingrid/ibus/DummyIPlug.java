/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import de.ingrid.iplug.IPlug;
import de.ingrid.iplug.PlugDescription;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;

/**
 * 
 */
public class DummyIPlug implements IPlug {

    public static final String SUMMARY = "a summary";
    public static final String TITLE = "a title";
    private String fMyPlugId;
    
    public DummyIPlug() {
     // for serialisations 
    }
    
    public DummyIPlug(String plugId) {
     this.fMyPlugId = plugId;
    }

    public IngridHits search(IngridQuery query, int start, int lenght) {
        return new IngridHits(this.fMyPlugId, 1, new IngridHit[] { new IngridHit(this.fMyPlugId, 23, 23, 0.23f) });
    }

    public void configure(PlugDescription arg0) throws Exception {
        // TODO Auto-generated method stub
    }

    public IngridHitDetail getDetails(IngridHit hit, IngridQuery ingridQuery)
            throws Exception {
        return new IngridHitDetail(hit, TITLE, SUMMARY);

    }
}
