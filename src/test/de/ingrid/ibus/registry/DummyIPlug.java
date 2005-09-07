/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.registry;

import de.ingrid.iplug.IIPlug;

/**
 * created on 21.07.2005 <p>
 *
 * @author hs
 */

public class DummyIPlug implements IIPlug {

    private String[] fFields;

    private String fId;
    
    /**
     * @param id
     * @param fields 
     */
    
    public DummyIPlug(String id, String[] fields) {
        this.fId = id;
        this.fFields = fields;

    }
    /**
     * @return The iplug id. 
     */
    public String getId() {
        return this.fId;
    }
    
    /**
     * @return The supported data fields
     */
    public String[] getFields() {
        return this.fFields;
    }
   

}
