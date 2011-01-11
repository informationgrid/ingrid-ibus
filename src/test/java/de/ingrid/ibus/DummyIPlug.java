/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import java.util.Random;

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

    /***/
    public static final String SUMMARY = "a summary";

    /***/
    public static final String TITLE = "a title";

    private String fMyPlugId;

    private PlugDescription fPlugDescription;
    
    private Random _random = new Random(System.currentTimeMillis());
    
    private float[] useScores = null;

    /**
     * 
     */
    public DummyIPlug() {
	// for serialisations
    }

    /**
     * @param plugId
     */
    public DummyIPlug(String plugId) {
        this.fMyPlugId = plugId;
    }
    
    public DummyIPlug(String plugId, float[] scores) {
        this.fMyPlugId = plugId;
        this.useScores = scores;
    }

    public IngridHits search(IngridQuery query, int start, int lenght) {
	IngridHit[] hit = null;
	if (query.getGrouped() != null && query.getGrouped().equals(IngridQuery.GROUPED_BY_DATASOURCE)) {
	    hit = new IngridHit[1];
	    hit[0] = new IngridHit(this.fMyPlugId, 23, 23, 23f);
	    hit[0].setGroupTotalHitLength(2);
	} else {
	    hit = new IngridHit[2];
	    hit[0] = new IngridHit(this.fMyPlugId, 23, 23, _random.nextInt());
	    hit[0].setGroupTotalHitLength(2);
	    hit[1] = new IngridHit(this.fMyPlugId, 23, 23, _random.nextInt());
	    hit[1].setGroupTotalHitLength(2);
	}
	
	if (this.useScores != null)
	    addScores(hit);
	
	return new IngridHits(this.fMyPlugId, hit.length, hit, true);
    }

    private void addScores(IngridHit[] hit) {
        int loopSize = useScores.length <= hit.length ? useScores.length : hit.length;
        for (int i=0; i < loopSize; i++) {
            hit[i].setScore(useScores[i]);
        }
    }

    public void configure(PlugDescription arg0) throws Exception {
	this.fPlugDescription = arg0;
    }

    public IngridHitDetail getDetail(IngridHit hit, IngridQuery ingridQuery, String[] fields) throws Exception {
	IngridHitDetail detail = new IngridHitDetail(hit, TITLE, SUMMARY);
	for (int i = 0; i < fields.length; i++) {
	    if (fields[i].equals(PlugDescription.PARTNER)) {
		detail.setArray(PlugDescription.PARTNER, this.fPlugDescription.getPartners());
	    } else if (fields[i].equals(PlugDescription.PROVIDER)) {
		detail.setArray(PlugDescription.PROVIDER, this.fPlugDescription.getProviders());
	    }
	}
	return detail;

    }

    public IngridHitDetail[] getDetails(IngridHit[] hits, IngridQuery query, String[] requestedFields) throws Exception {
	return new IngridHitDetail[] { new IngridHitDetail(hits[0], TITLE, SUMMARY) };
    }

    public void close() throws Exception {
	// TODO Auto-generated method stub

    }
}
