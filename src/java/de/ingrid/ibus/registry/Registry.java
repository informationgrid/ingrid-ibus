/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.registry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import net.weta.components.communication.ICommunication;
import net.weta.components.communication.WetagURL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.ibus.net.IPlugProxyFactory;
import de.ingrid.utils.IPlug;
import de.ingrid.utils.PlugDescription;

/**
 * 
 * created on 21.07.2005
 * <p>
 * 
 * @author hs
 */

public class Registry {

    private static final String LAST_LIFESIGN = "addedTimeStamp";

    private static Log fLogger = LogFactory.getLog(Registry.class);

    private ICommunication fCommunication;

    private IPlugProxyFactory fProxyFactory;

    private HashMap fPlugProxyByPlugId = new HashMap();

    private HashMap fPlugDescriptionByPlugId = new HashMap();

    private boolean fIplugAutoActivation;

    private long fLifeTime;

    private HashMap fGlobalRanking;

    private String fBusUrl;

    private Properties fActivatedIplugs;

    private File fFile;

    /**
     * @param lifeTimeOfPlugs
     * @param iplugAutoActivation
     * @param factory
     */
    public Registry(long lifeTimeOfPlugs, boolean iplugAutoActivation, IPlugProxyFactory factory) {
        try {
            File dir = new File("conf");
            if (!dir.exists()) {
                dir.mkdir();
            }
            this.fFile = new File(dir, "activatedIplugs.properties");
            if (!this.fFile.exists()) {
                this.fFile.createNewFile();
            }
        } catch (Exception e) {
            fLogger.error("Cannot open the file for saving the activation state of the iplugs.", e);
        }

        loadProperties();
        this.fLifeTime = lifeTimeOfPlugs;
        this.fIplugAutoActivation = iplugAutoActivation;
        this.fProxyFactory = factory;
    }

    /**
     * Adds a iplug to the registry.
     * 
     * @param plugDescription
     */
    public void addPlugDescription(PlugDescription plugDescription) {
        removePlug(plugDescription.getPlugId());
        if (this.fBusUrl == null) {
            joinGroup(plugDescription.getProxyServiceURL());
        }
        if (plugDescription.getMd5Hash() == null) {
            throw new IllegalArgumentException("md5 hash not set - plug '" + plugDescription.getPlugId());
        }

        if (this.fActivatedIplugs.containsKey(plugDescription.getProxyServiceURL())) {
            final String activated = (String) this.fActivatedIplugs.get(plugDescription.getProxyServiceURL());
            if (activated.equals("true")) {
                plugDescription.setActivate(true);
            } else {
                plugDescription.setActivate(false);
            }
        } else {
            plugDescription.setActivate(this.fIplugAutoActivation);
        }
        plugDescription.putLong(LAST_LIFESIGN, System.currentTimeMillis());
        createPlugProxy(plugDescription);
        synchronized (this.fPlugDescriptionByPlugId) {
            this.fPlugDescriptionByPlugId.put(plugDescription.getPlugId(), plugDescription);
        }
    }

    /**
     * @param plugId
     * @param md5Hash
     * @return true if registry contains a plug with given hash
     */
    public boolean containsPlugDescription(String plugId, String md5Hash) {
        PlugDescription plugDescription = getPlugDescription(plugId);
        if (plugDescription == null) {
            return false;
        }
        plugDescription.putLong(LAST_LIFESIGN, System.currentTimeMillis());

        return plugDescription.getMd5Hash().equals(md5Hash);
    }

    private void joinGroup(String proxyServiceUrl) {
        if (this.fCommunication == null) {
            // for tests
            return;
        }
        try {
            WetagURL wetagURL = new WetagURL(proxyServiceUrl);
            this.fCommunication.subscribeGroup(wetagURL.getGroupPath());
        } catch (Exception e) {
            IllegalStateException exception = new IllegalStateException("could not join plug group of plug '"
                    + proxyServiceUrl + '\'');
            exception.initCause(e);
            throw exception;
        }
    }

    private void createPlugProxy(PlugDescription plugDescription) {
        String plugId = plugDescription.getPlugId();
        IPlug plugProxy;
        try {
            plugProxy = this.fProxyFactory.createPlugProxy(plugDescription, this.fBusUrl);
            synchronized (this.fPlugProxyByPlugId) {
                this.fPlugProxyByPlugId.put(plugDescription.getPlugId(), plugProxy);
            }
        } catch (Exception e) {
            fLogger.error("(REMOVING IPLUG '" + plugId + "' !): could not create proxy object: ", e);
            removePlug(plugId);
            IllegalStateException iste = new IllegalStateException("plug with id '" + plugId
                    + "' currently not availible");
            iste.initCause(e);
            throw iste;
        }

        // establish connection
        try {
            plugProxy.toString();
        } catch (Exception e) {
            // sometimes there seems to be a message loss shortly after
            // connection establishment
            try {
                Thread.sleep(1500);
                plugProxy.toString();
            } catch (InterruptedException e1) {
                fLogger.warn("The sleep during iplug connection is interrupted.");
            }
        }
    }

    /**
     * removes a iplug from cache, e.g. if the connection permanent fails.
     * 
     * @param plugId
     */
    public void removePlug(String plugId) {
        synchronized (this.fPlugProxyByPlugId) {
            this.fPlugProxyByPlugId.remove(plugId);
        }
        PlugDescription description;
        synchronized (this.fPlugDescriptionByPlugId) {
            description = (PlugDescription) this.fPlugDescriptionByPlugId.remove(plugId);
        }
        if (description != null && this.fCommunication != null) {
            try {
                String plugUrl = null;
                if (this.fBusUrl != null) {
                    final String path = this.fBusUrl.replaceFirst("/(.*)?:(.*)?", "$1");
                    final String peername = description.getProxyServiceURL().replaceFirst("/(.*)?:(.*)?", "$2");
                    plugUrl = '/' + path + ':' + peername;
                } else {
                    plugUrl = description.getProxyServiceURL();
                }
                this.fCommunication.closeConnection(plugUrl);
            } catch (IOException e) {
                fLogger.warn("problems on closing connection", e);
            }
        }
    }

    /**
     * @param id
     * @return the iplug by key or <code>null</code>
     */
    public PlugDescription getPlugDescription(String id) {
        PlugDescription result;

        synchronized (this.fPlugDescriptionByPlugId) {
            result = (PlugDescription) this.fPlugDescriptionByPlugId.get(id);
        }

        return result;
    }

    /**
     * @return all registed iplugs without checking the time stamp
     */
    public PlugDescription[] getAllIPlugsWithoutTimeLimitation() {
        Collection plugDescriptions;

        synchronized (this.fPlugDescriptionByPlugId) {
            plugDescriptions = this.fPlugDescriptionByPlugId.values();
        }

        return (PlugDescription[]) plugDescriptions.toArray(new PlugDescription[plugDescriptions.size()]);
    }

    /**
     * @return all registed iplugs younger than given lifetime
     */
    public PlugDescription[] getAllIPlugs() {
        PlugDescription[] plugDescriptions = getAllIPlugsWithoutTimeLimitation();
        List plugs = new ArrayList(plugDescriptions.length);
        long now = System.currentTimeMillis();
        for (int i = 0; i < plugDescriptions.length; i++) {
            if (plugDescriptions[i].getLong(LAST_LIFESIGN) + this.fLifeTime > now) {
                plugs.add(plugDescriptions[i]);
            }
        }
        return (PlugDescription[]) plugs.toArray(new PlugDescription[plugs.size()]);
    }

    /**
     * @param plugId
     * @return the plug proxy
     */
    public IPlug getPlugProxy(String plugId) {
        synchronized (this.fPlugProxyByPlugId) {
            return (IPlug) this.fPlugProxyByPlugId.get(plugId);
        }
    }

    private void saveProperties() {
        try {
            FileOutputStream fos = new FileOutputStream(this.fFile);
            this.fActivatedIplugs.store(fos, "activated iplugs");
            fos.close();
        } catch (IOException e) {
            fLogger.error("Cannot save the activation properties.", e);
        }
    }

    private void loadProperties() {
        try {
            FileInputStream fis = new FileInputStream(this.fFile);
            this.fActivatedIplugs = new Properties();
            this.fActivatedIplugs.load(fis);
            fis.close();
        } catch (IOException e) {
            fLogger.error("Cannot load the activation properties.", e);
        }
    }

    /**
     * Activate an IPlug.
     * 
     * @param plugId
     * @throws IllegalArgumentException
     *             if plugId is unknown
     */
    public void activatePlug(String plugId) {
        PlugDescription plugDescription = getPlugDescription(plugId);
        if (plugDescription != null) {
            plugDescription.activate();
            this.fActivatedIplugs.setProperty(plugId, "true");
            saveProperties();
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
    public void deActivatePlug(String plugId) {
        PlugDescription plugDescription = getPlugDescription(plugId);
        if (plugDescription != null) {
            plugDescription.deActivate();
            this.fActivatedIplugs.setProperty(plugId, "false");
            saveProperties();
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
     * @param plugId
     *            A iplug id.
     * @return The boost factor to a iplug.
     */
    public Float getGlobalRankingBoost(String plugId) {
        Float result = null;
        if (null != this.fGlobalRanking) {
            result = (Float) this.fGlobalRanking.get(plugId);
        }

        return result;
    }

    /**
     * @param busurl
     */
    public void setUrl(final String busurl) {
        this.fBusUrl = busurl;
    }
}
