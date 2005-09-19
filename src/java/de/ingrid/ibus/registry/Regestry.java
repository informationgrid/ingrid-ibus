/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.registry;

import java.util.ArrayList;

import de.ingrid.iplug.IPlug;
import de.ingrid.iplug.PlugDescription;

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
    public void addIPlug(PlugDescription plug) {
        putToCache(plug);
    }

    private void putToCache(PlugDescription plug) {
        this.fIPlugs.add(plug);
    }

    /**
     * @param id
     * @return the iplug by key or <code>null</code>
     */
    public PlugDescription getIPlug(String id) {
        int count = this.fIPlugs.size();
        for (int i = 0; i < count; i++) {
            PlugDescription plug = (PlugDescription) this.fIPlugs.get(i);
            if (plug.getPlugId().equals(id)) {
                return plug;
            }
        }
        return null;
    }

    /**
     * @return all registed iplugs
     */
    public PlugDescription[] getAllIPlugs() {
        return (PlugDescription[]) this.fIPlugs.toArray(new PlugDescription[this.fIPlugs.size()]);
    }

}
