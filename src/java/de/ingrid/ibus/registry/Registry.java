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
 * A IPlug registry. All connected IPlugs are registered and by default are deactivated.
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
     * Creates a registry with a given lifetime for IPlugs, a given auto activation value for new IPlugs and a IPlug
     * factory.
     * 
     * @param lifeTimeOfPlugs
     *            Life time of IPlugs. If the last life sign of a IPlug is longer than this value the IPlug is removed.
     * @param iplugAutoActivation
     *            The auto activation feature. If this is true all new IPlugs are activated by default otherwise not.
     * @param factory
     *            The factory that creates IPlugs.
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
            if (fLogger.isErrorEnabled()) {
                fLogger.error("Cannot open the file for saving the activation state of the iplugs.", e);
            }
        }

        loadProperties();
        this.fLifeTime = lifeTimeOfPlugs;
        this.fIplugAutoActivation = iplugAutoActivation;
        this.fProxyFactory = factory;
    }

    /**
     * Adds a IPlug to the registry.
     * 
     * @param plugDescription
     *            The PlugDescrption of the IPlug. Changed PlugDescrptions are updated.
     */
    public void addPlugDescription(PlugDescription plugDescription) {
        if (null != plugDescription) {
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
        } else {
            if (fLogger.isErrorEnabled()) {
                fLogger.error("Cannot add IPlug: plugdescription is null.");
            }
        }
    }

    /**
     * Tests whether a PlugDescription already exists and sets a new value for the last life sign.
     * 
     * @param plugId
     *            The id of the IPlug.
     * @param md5Hash
     *            The MD5 hash of the new plugdescrption.
     * @return True if the registry contains a IPlug with the given hash.
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
            if (fLogger.isErrorEnabled()) {
                fLogger.error("(REMOVING IPLUG '" + plugId + "' !): could not create proxy object: ", e);
            }
            removePlug(plugId);
            closeConnectionToIplug(plugDescription);
            IllegalStateException iste = new IllegalStateException("plug with id '" + plugId
                    + "' currently not available");
            iste.initCause(e);
            throw iste;
        }

        // establish connection
        try {
            fLogger.info("establish connection: " + plugProxy.toString());
        } catch (Exception e) {
            fLogger.error("error establish connection " + plugId, e);
        }
    }

    /**
     * Removes a IPlug from cache, e.g. if the connection permanently fails.
     * 
     * @param plugId
     *            The id of the IPlug that fails.
     */
    public void removePlug(String plugId) {
        synchronized (this.fPlugProxyByPlugId) {
            this.fPlugProxyByPlugId.remove(plugId);
        }
        synchronized (this.fPlugDescriptionByPlugId) {
            this.fPlugDescriptionByPlugId.remove(plugId);
        }
    }
    
    private void closeConnectionToIplug(PlugDescription plugDescription) {
        if (plugDescription != null && this.fCommunication != null) {
            try {
                String plugUrl = plugDescription.getProxyServiceURL();
                this.fCommunication.closeConnection(plugUrl);
            } catch (IOException e) {
                if (fLogger.isWarnEnabled()) {
                    fLogger.warn("problems on closing connection", e);
                }
            }
        }        
    }

    /**
     * Returns a PlugDescrption to an IPlug id.
     * 
     * @param id
     *            The IPlug id to that a PlugDescrption should be returned.
     * @return The IPlug to the id or <code>null</code> if doesn't exist.
     */
    public PlugDescription getPlugDescription(String id) {
        PlugDescription result;

        synchronized (this.fPlugDescriptionByPlugId) {
            result = (PlugDescription) this.fPlugDescriptionByPlugId.get(id);
        }

        return result;
    }

    /**
     * Returns all registered IPlugs.
     * 
     * @return All registered IPlugs without checking the time stamp.
     */
    public PlugDescription[] getAllIPlugsWithoutTimeLimitation() {
        Collection plugDescriptions;

        synchronized (this.fPlugDescriptionByPlugId) {
            plugDescriptions = this.fPlugDescriptionByPlugId.values();
        }

        return (PlugDescription[]) plugDescriptions.toArray(new PlugDescription[plugDescriptions.size()]);
    }

    /**
     * Returns all IPlugs that are still alive.
     * 
     * @return All registered IPlugs younger than the given life time.
     */
    public PlugDescription[] getAllIPlugs() {
        PlugDescription[] plugDescriptions = getAllIPlugsWithoutTimeLimitation();
        List plugs = new ArrayList(plugDescriptions.length);
        long now = System.currentTimeMillis();
        for (int i = 0; i < plugDescriptions.length; i++) {
            if (plugDescriptions[i].getLong(LAST_LIFESIGN) + this.fLifeTime > now) {
                plugs.add(plugDescriptions[i]);
            } else {
                removePlug(plugDescriptions[i].getPlugId());
                closeConnectionToIplug(plugDescriptions[i]);
            }
        }
        return (PlugDescription[]) plugs.toArray(new PlugDescription[plugs.size()]);
    }

    /**
     * Returns a IPlug proxy to a given IPlug id.
     * 
     * @param plugId
     *            A IPlug id to that the IPlug proxy should be returned.
     * @return The IPlug proxy.
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
            if (fLogger.isErrorEnabled()) {
                fLogger.error("Cannot save the activation properties.", e);
            }
        }
    }

    private void loadProperties() {
        try {
            FileInputStream fis = new FileInputStream(this.fFile);
            this.fActivatedIplugs = new Properties();
            this.fActivatedIplugs.load(fis);
            fis.close();
        } catch (IOException e) {
            if (fLogger.isErrorEnabled()) {
                fLogger.error("Cannot load the activation properties.", e);
            }
        }
    }

    /**
     * Activates the IPlug to the given IPlug id.
     * 
     * @param plugId
     *            A IPlug id from the IPlug that should be activated.
     * @throws IllegalArgumentException
     *             If the IPlug is unknown.
     */
    public void activatePlug(String plugId) {
        PlugDescription plugDescription = getPlugDescription(plugId);
        if (plugDescription != null) {
            plugDescription.activate();
            this.fActivatedIplugs.setProperty(plugId, "true");
            saveProperties();
        } else {
            throw new IllegalArgumentException("iplug unknown: ".concat(plugId));
        }
    }

    /**
     * Deactivates the IPlug to the given IPlug id.
     * 
     * @param plugId
     *            A IPlug id from the IPlug that should be deactivated.
     * @throws IllegalArgumentException
     *             If the IPlugId is unknown.
     */
    public void deActivatePlug(String plugId) {
        PlugDescription plugDescription = getPlugDescription(plugId);
        if (plugDescription != null) {
            plugDescription.deActivate();
            this.fActivatedIplugs.setProperty(plugId, "false");
            saveProperties();
        } else {
            throw new IllegalArgumentException("iplug unknown: ".concat(plugId));
        }
    }

    /**
     * Returns a communication object to connect the IPlugs.
     * 
     * @return The communication object to connect the IPlugs.
     */
    public ICommunication getCommunication() {
        return this.fCommunication;
    }

    /**
     * Sets a communication object to connect the IPlugs.
     * 
     * @param communication
     *            The communication object to connect the IPlugs.
     */
    public void setCommunication(ICommunication communication) {
        this.fCommunication = communication;
    }

    /**
     * Sets the global ranking for all IPlugs.
     * 
     * @param globalRanking
     *            A HashMap containing a boost factor to a IPlug id.
     */
    public void setGlobalRanking(HashMap globalRanking) {
        this.fGlobalRanking = globalRanking;
    }

    /**
     * Returns a ranking boost for a given IPlug id.
     * 
     * @param plugId
     *            A boosting for IPlug id.
     * @return The boost factor to a IPlug.
     */
    public Float getGlobalRankingBoost(String plugId) {
        Float result = null;
        if (null != this.fGlobalRanking) {
            result = (Float) this.fGlobalRanking.get(plugId);
        }

        return result;
    }

    /**
     * Sets the bus url the IPlugs connected with.
     * 
     * @param busurl
     *            The bus url. The form looks like /<group name>:<bus name>
     */
    public void setUrl(final String busurl) {
        this.fBusUrl = busurl;
    }
}
