/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.registry;

import java.util.ArrayList;

import de.ingrid.iplug.IIPlug;

/**
 * 
 * created on 21.07.2005
 * <p>
 * 
 * @author hs
 */

public class Regestry {

    private ArrayList fIPlugs = new ArrayList();

    /**
     * Adds a iplug to the registry
     * 
     * @param plug
     */
    public void addIPlug(IIPlug plug) {
        putToCache(plug);
    }

    private void putToCache(IIPlug plug) {
        this.fIPlugs.add(plug);
    }

    /**
     * @param id
     * @return the iplug by key or <code>null</code>
     */
    public IIPlug getIPlug(String id) {
        int count = this.fIPlugs.size();
        for (int i = 0; i < count; i++) {
            IIPlug plug = (IIPlug) this.fIPlugs.get(i);
            if (plug.getId().equals(id)) {
                return plug;
            }
        }
        return null;
    }

    /**
     * @return all registed iplugs
     */
    public IIPlug[] getAllIPlugs() {
        return (IIPlug[]) this.fIPlugs.toArray(new IIPlug[this.fIPlugs.size()]);
    }

}
