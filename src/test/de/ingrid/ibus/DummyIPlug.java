/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import de.ingrid.utils.IPlug;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
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
        return new IngridHits(this.fMyPlugId, 1, new IngridHit[] { new IngridHit(this.fMyPlugId, 23, 23, 23f) }, true);
    }

    public void configure(PlugDescription arg0) throws Exception {
        // TODO Auto-generated method stub
    }

    public IngridHitDetail getDetail(IngridHit hit, IngridQuery ingridQuery, String[] fields) throws Exception {
        return new IngridHitDetail(hit, TITLE, SUMMARY);

    }

    public IngridHitDetail[] getDetails(IngridHit[] hits, IngridQuery query, String[] requestedFields) throws Exception {
        return new IngridHitDetail[] { new IngridHitDetail(hits[0], TITLE, SUMMARY) };
    }

	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
