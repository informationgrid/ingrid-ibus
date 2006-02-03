/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.registry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import de.ingrid.iplug.IPlug;
import de.ingrid.iplug.PlugDescription;

/**
 * 
 * created on 21.07.2005
 * <p>
 * 
 * @author hs
 */

public class Registry implements Serializable {

    private static final String ADDING_TIMESTAMP = "addedTimeStamp";

    private ArrayList fIPlugs = new ArrayList();

    private long fLifeTime = 10000;

    private ArrayList fIPlugListener = new ArrayList();

    private HashMap fPlugProxyCache = new HashMap();

    /**
     * @param lifeTimeOfPlugs
     */
    public Registry(long lifeTimeOfPlugs) {
        this.fLifeTime = lifeTimeOfPlugs;
    }

    /**
     * Adds a iplug to the registry.
     * 
     * @param plug
     */
    public void addIPlug(PlugDescription plug) {
        putToCache(plug);
    }

    private void putToCache(PlugDescription plug) {
        String id = plug.getPlugId();
        removePlugFromCache(id);
        plug.putLong(ADDING_TIMESTAMP, System.currentTimeMillis());
        this.fIPlugs.add(plug);
    }

    /**
     * removes a iplug from cache, e.g. if the connection permanent fails.
     * 
     * @param plugId
     */
    public void removePlugFromCache(String plugId) {
        fPlugProxyCache.remove(plugId);
        for (Iterator iter = this.fIPlugs.iterator(); iter.hasNext();) {
            PlugDescription element = (PlugDescription) iter.next();
            String elementId = element.getPlugId();
            if ((null != elementId) && (null != plugId)) {
                if (elementId.equals(plugId)) {
                    iter.remove();
                }
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
     * @deprecated
     */
    public PlugDescription[] getAllIPlugsWithoutTimeLimitation() {
        return (PlugDescription[]) this.fIPlugs
                .toArray(new PlugDescription[this.fIPlugs.size()]);
    }

    /**
     * @return all registed iplugs younger than given lifetime
     */
    public PlugDescription[] getAllIPlugs() {
        ArrayList list = new ArrayList();
        PlugDescription[] descriptions = (PlugDescription[]) this.fIPlugs
                .toArray(new PlugDescription[this.fIPlugs.size()]);
        long maxLifeTime = System.currentTimeMillis();
        for (int i = 0; i < descriptions.length; i++) {
            if (descriptions[i].getLong(ADDING_TIMESTAMP) + this.fLifeTime > maxLifeTime) {
                list.add(descriptions[i]);
            }
        }
        return (PlugDescription[]) list
                .toArray(new PlugDescription[list.size()]);

    }

    /**
     * @param iPlugListener
     */
    public void addIPlugListener(IPlugListener iPlugListener) {
        this.fIPlugListener.add(iPlugListener);
    }

    /**
     * @param iPlugListener
     */
    public void removeIPlugListener(IPlugListener iPlugListener) {
        this.fIPlugListener.remove(iPlugListener);
    }

    public IPlug getProxyFromCache(String plugId) {
        return (IPlug) this.fPlugProxyCache.get(plugId);
    }

    public void addProxyToCache(String plugId, IPlug plugProxy) {
        this.fPlugProxyCache.put(plugId, plugProxy);
    }

}
