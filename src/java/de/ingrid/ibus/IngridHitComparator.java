/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import java.util.Comparator;

import de.ingrid.utils.IngridHit;

/**
 * compares the score
 * 
 * created on 09.08.2005
 * 
 * @author sg
 * @version $Revision: 1.3 $
 */
public class IngridHitComparator implements Comparator {

    public int compare(Object arg0, Object arg1) {
        if (null == arg0) {
            return -1;
        }
        
        if (null == arg1) {
            return 1;
        }
        
        IngridHit hit0 = (IngridHit) arg0;
        IngridHit hit1 = (IngridHit) arg1;

        float score0 = hit0.getScore();
        float score1 = hit1.getScore();
        float scoreDiff = score0 - score1;

        return (scoreDiff > 0) ? -1 : 1;
    }
}
