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

import net.weta.components.communication.ICommunication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.utils.IPlug;
import de.ingrid.utils.PlugDescription;

/**
 * 
 * created on 21.07.2005
 * <p>
 * 
 * @author hs
 */

public class Registry implements Serializable {

    private static final long serialVersionUID = Registry.class.getName().hashCode();

    private static final String ADDING_TIMESTAMP = "addedTimeStamp";

    private ArrayList fIPlugs = new ArrayList();

    private long fLifeTime = 10000;

    private ArrayList fIPlugListener = new ArrayList();

    private HashMap fPlugProxyCache = new HashMap();

    private boolean fIplugAutoActivation;

    private ICommunication fCommunication = null;

    private HashMap fGlobalRanking = null;

    private static Log fLogger = LogFactory.getLog(Registry.class);

    /**
     * @param lifeTimeOfPlugs
     * @param iplugAutoActivation
     */
    public Registry(long lifeTimeOfPlugs, boolean iplugAutoActivation) {
        this.fLifeTime = lifeTimeOfPlugs;
        this.fIplugAutoActivation = iplugAutoActivation;
    }

    /**
     * Adds a iplug to the registry.
     * 
     * @param plug
     */
    public void addIPlug(PlugDescription plug) {
        if (this.fIplugAutoActivation) {
            plug.activate();
        } else {
            plug.deActivate();
        }

        if (null != this.fCommunication) {
            try {
                this.fCommunication.subscribeGroup(plug.getProxyServiceURL());
            } catch (Exception e) {
                fLogger.error(e.getMessage(), e);
            }
        } else {
            // fLogger.error("The communication isn't set in the registry.");
        }

        putToCache(plug);
    }

    private void putToCache(PlugDescription plug) {
        String id = plug.getPlugId();
        if (getPlugDescription(id) == null) {
            removePlugFromCache(id);
            plug.putLong(ADDING_TIMESTAMP, System.currentTimeMillis());
            this.fIPlugs.add(plug);
        } else {
            // just update time stamp
            getPlugDescription(id).putLong(ADDING_TIMESTAMP, System.currentTimeMillis());
        }
    }

    /**
     * removes a iplug from cache, e.g. if the connection permanent fails.
     * 
     * @param plugId
     */
    public void removePlugFromCache(String plugId) {
        synchronized (this.fPlugProxyCache) {
            this.fPlugProxyCache.remove(plugId);
        }
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
    public PlugDescription getPlugDescription(String id) {
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

    /**
     * @param plugId
     * @return the plug proxy
     */
    public IPlug getProxyFromCache(String plugId) {
        synchronized (this.fPlugProxyCache) {
            return (IPlug) this.fPlugProxyCache.get(plugId);
        }
    }

    /**
     * @param plugId
     * @param plugProxy
     */
    public void addProxyToCache(String plugId, IPlug plugProxy) {
        synchronized (this.fPlugProxyCache) {
            this.fPlugProxyCache.put(plugId, plugProxy);
        }
    }

    /**
     * activate a plug
     * 
     * @param plugId
     * @throws IllegalArgumentException
     *             if plugId is unknown
     */
    public void activatePlug(String plugId) throws IllegalArgumentException {
        PlugDescription plugDescription = getPlugDescription(plugId);
        if (plugDescription != null) {
            plugDescription.activate();
        } else {
            throw new IllegalArgumentException("iplug unknown");
        }
    }

    /**
     * deActivate a plug
     * 
     * @param plugId
     * @throws IllegalArgumentException
     *             if plugId is unknown
     */
    public void deActivatePlug(String plugId) throws IllegalArgumentException {
        PlugDescription plugDescription = getPlugDescription(plugId);
        if (plugDescription != null) {
            plugDescription.deActivate();
        } else {
            throw new IllegalArgumentException("iplug unknown");
        }
    }

    /**
     * @return the communication
     */
    public ICommunication getCommunication() {
        return this.fCommunication;
    }

    /**
     * @param communication
     */
    public void setCommunication(ICommunication communication) {
        this.fCommunication = communication;
    }

    /**
     * Sets the global ranking for all iplugs.
     * 
     * @param globalRanking
     *            A HashMap containing a boost factor to a iplug id.
     */
    public void setGlobalRanking(HashMap globalRanking) {
        this.fGlobalRanking = globalRanking;
    }

    /**
     * @param plugId A iplug id.
     * @return The boost factor to a iplug.
     */
    public Float getGlobalRankingBoost(String plugId) {
        return (Float) this.fGlobalRanking.get(plugId);
    }
}
