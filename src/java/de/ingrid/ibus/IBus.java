/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.query.IngridQuery;

/**
 * Interface defining the search interface for the Ibus
 * 
 * created on 19.09.2005
 * 
 * @author sg
 * @version $Revision: 1.2 $
 * 
 */
public interface IBus {
    /**
     * @param query
     * @param hitsPerPage
     * @param currentPage
     * @param length
     * @param maxMilliseconds
     * @return array of matching documents.
     * @throws Exception
     */
    public IngridDocument[] search(IngridQuery query, int hitsPerPage, int currentPage, int length, int maxMilliseconds)
            throws Exception;

}
