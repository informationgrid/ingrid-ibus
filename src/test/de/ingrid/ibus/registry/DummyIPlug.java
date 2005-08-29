/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.registry;

import de.ingrid.iplug.IIPlug;

public class DummyIPlug implements IIPlug {

    private String[] fFields;

    private String fId;

    public DummyIPlug(String id, String[] string) {
        this.fId = id;
        this.fFields = string;

    }

    public String geId() {
        return this.fId;
    }

}
