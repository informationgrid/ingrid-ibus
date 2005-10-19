/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.registry;

import java.util.ArrayList;

import de.ingrid.iplug.PlugDescription;

/**
 * 
 * created on 21.07.2005
 * <p>
 * 
 * @author hs
 */

public class Registry {

    private static final String ADDING_TIMESTAMP = "addedTimeStamp";

    private ArrayList fIPlugs = new ArrayList();

    private long fLifeTime = 10000;

    /**
     * @param lifeTimeOfPlugs
     */
    public Registry(long lifeTimeOfPlugs) {
        this.fLifeTime = lifeTimeOfPlugs;
    }

    /**
     * Adds a iplug to the registry
     * 
     * @param plug
     */
    public void addIPlug(PlugDescription plug) {
        putToCache(plug);
    }

    private void putToCache(PlugDescription plug) {
        String id = plug.getPlugId();
        removeFromCache(id);
        plug.putLong(ADDING_TIMESTAMP, System.currentTimeMillis());
        this.fIPlugs.add(plug);
    }

    private void removeFromCache(String id) {
        int count = this.fIPlugs.size();
        for (int i = 0; i < count; i++) {
            PlugDescription plug = (PlugDescription) this.fIPlugs.get(i);
            if (plug.getPlugId().equals(id)) {
                this.fIPlugs.remove(i);
                break;
            }
        }

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
     * @return all registed iplugs without checking the time stamp
     */
    public PlugDescription[] getAllIPlugsWithoutTimeLimitation() {
        return (PlugDescription[]) this.fIPlugs.toArray(new PlugDescription[this.fIPlugs.size()]);
    }

    /**
     * @return all registed iplugs younger than given lifetime
     */
    public PlugDescription[] getAllIPlugs() {
        ArrayList list = new ArrayList();
        PlugDescription[] descriptions = (PlugDescription[]) this.fIPlugs.toArray(new PlugDescription[this.fIPlugs
                .size()]);
        long maxLifeTime = System.currentTimeMillis();
        for (int i = 0; i < descriptions.length; i++) {
            if (descriptions[i].getLong(ADDING_TIMESTAMP) + this.fLifeTime > maxLifeTime) {
                list.add(descriptions[i]);
            }
        }
        return (PlugDescription[]) list.toArray(new PlugDescription[list.size()]);

    }

}
