/*
 * **************************************************-
 * InGrid iBus
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/*
 * Copyright (c) 1997-2005 by media style GmbH
 *
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.comm.registry;

import de.ingrid.ibus.comm.net.IPlugProxyFactory;
import de.ingrid.ibus.management.ManagementService;
import de.ingrid.ibus.service.SearchService;
import de.ingrid.utils.IPlug;
import de.ingrid.utils.IngridCall;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.PlugDescription;
import net.weta.components.communication.ICommunication;
import net.weta.components.communication.WetagURL;
import net.weta.components.communication.util.PooledThreadExecutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * A IPlug registry. All connected IPlugs are registered and by default are deactivated.
 */
public class Registry {

    public static final String LAST_LIFESIGN = "addedTimeStamp";

    private static Log fLogger = LogFactory.getLog(Registry.class);

    private ICommunication fCommunication;

    private IPlugProxyFactory fProxyFactory;

    private final HashMap<String, IPlug> fPlugProxyByPlugId = new HashMap<>();

    private HashMap<String, PlugDescription> fPlugDescriptionByPlugId = new HashMap<>();

    private List<String> iPlugsNotUsingCentralIndex = new ArrayList<>();

    private boolean fIplugAutoActivation;

    private long fLifeTime;

    private HashMap<String, Float> fGlobalRanking;

    private String fBusUrl;

    private Properties fActivatedIplugs;

    private File fFile;

    private class RegistryIPlugTimeoutScanner extends Thread {

        @Override
        public void run() {
            while (!this.isInterrupted()) {
                try {
                    sleep(fLifeTime);
                    if (fLogger.isInfoEnabled()) {
                        fLogger.info("Check for timed out iPlugs.");
                    }
                    removeIPlugsWithTimeout();
                } catch (InterruptedException e) {
                    fLogger.warn("Timeout iPlug scanner has been interrupted and shut down!");
                }
            }
        }
    }


    /**
     * Creates a registry with a given lifetime for IPlugs, a given auto activation value for new IPlugs and a IPlug
     * factory.
     *
     * @param lifeTimeOfPlugs     Life time of IPlugs. If the last life sign of a IPlug is longer than this value the IPlug is removed.
     * @param iplugAutoActivation The auto activation feature. If this is true all new IPlugs are activated by default otherwise not.
     * @param factory             The factory that creates IPlugs.
     */
    public Registry(long lifeTimeOfPlugs, boolean iplugAutoActivation, IPlugProxyFactory factory) {

        ClassPathResource ibusSettings = new ClassPathResource("/activatedIplugs.properties");

        try {
            if (ibusSettings.exists()) {
                this.fFile = ibusSettings.getFile();
            } else {
                File dir = new File("conf");
                if (!dir.exists()) {
                    dir.mkdir();
                }
                this.fFile = new File(dir, "activatedIplugs.properties");
                if (!this.fFile.exists()) {
                    this.fFile.createNewFile();
                }

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

        // start iplug timeout scanner
        PooledThreadExecutor.getInstance().execute(new RegistryIPlugTimeoutScanner());
    }

    /**
     * Adds a IPlug to the registry.
     *
     * @param plugDescription The PlugDescrption of the IPlug. Changed PlugDescrptions are updated.
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

            // iPlug is active in central index
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

            Object overrideProxy = plugDescription.get("overrideProxy");
            if (overrideProxy != null) {
                this.fPlugProxyByPlugId.put(plugDescription.getPlugId(), (IPlug) overrideProxy);
            } else {
                createPlugProxy(plugDescription);
            }

            synchronized (this.fPlugDescriptionByPlugId) {
                this.fPlugDescriptionByPlugId.put(plugDescription.getPlugId(), plugDescription);
                if (fLogger.isDebugEnabled()) {
                    fLogger.debug("Plugdescription added for iPlug: " + plugDescription.getProxyServiceURL());
                    fLogger.debug("plugfields: " + Arrays.asList(plugDescription.getFields()));
                }
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
     * @param plugId  The id of the IPlug.
     * @param md5Hash The MD5 hash of the new plugdescrption.
     * @return True if the registry contains a IPlug with the given hash.
     */
    public boolean containsPlugDescription(String plugId, String md5Hash) {
        PlugDescription plugDescription = getPlugDescription(plugId);
        if (plugDescription == null) {
            fLogger
                    .warn("plugdescription not found. do not update last lifesign: "
                            + plugId);
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
            synchronized (this.iPlugsNotUsingCentralIndex) {
                Object useRemoteElasticsearch = plugDescription.get("useRemoteElasticsearch");
                if (useRemoteElasticsearch == null || !((boolean) useRemoteElasticsearch)) {
                    this.iPlugsNotUsingCentralIndex.add(plugDescription.getPlugId());
                }
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
            fLogger.info("establish connection [" + plugId + "] ...");
            plugProxy.toString();
            fLogger.info("... success [" + plugId + "]");
        } catch (Exception e) {
            fLogger.error("... fails [" + plugId + "]", e);
        }
    }

    /**
     * Removes a IPlug from cache, e.g. if the connection permanently fails.
     *
     * @param plugId The id of the IPlug that fails.
     */
    public void removePlug(String plugId) {
        // do not remove local iPlugs which are always connected
        if (SearchService.CENTRAL_INDEX_ID.equals(plugId) || ManagementService.MANAGEMENT_IPLUG_ID.equals(plugId)) {
            fLogger.debug("Do not remove iPlug because it's always connected: " + plugId);
            return;
        }

        synchronized (this.fPlugProxyByPlugId) {
            this.fPlugProxyByPlugId.remove(plugId);
        }
        synchronized (this.fPlugDescriptionByPlugId) {
            this.fPlugDescriptionByPlugId.remove(plugId);
        }
        synchronized (this.iPlugsNotUsingCentralIndex) {
            iPlugsNotUsingCentralIndex.remove(plugId);
        }
    }

    private void closeConnectionToIplug(PlugDescription plugDescription) {
        if (plugDescription != null && this.fCommunication != null) {
            try {
                String plugUrl = plugDescription.getProxyServiceURL();
                fLogger.info("close connection to iplug: " + plugUrl);
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
     * @param id The IPlug id to that a PlugDescrption should be returned.
     * @return The IPlug to the id or <code>null</code> if doesn't exist.
     */
    public PlugDescription getPlugDescription(String id) {
        PlugDescription result;

        synchronized (this.fPlugDescriptionByPlugId) {
            boolean isManagementIPlug = ManagementService.MANAGEMENT_IPLUG_ID.equals(id);
            boolean isCentralSearchIPlug = SearchService.CENTRAL_INDEX_ID.equals(id);

            if (isManagementIPlug || isCentralSearchIPlug || this.iPlugsNotUsingCentralIndex.contains(id)) {
                result = this.fPlugDescriptionByPlugId.get(id);
            } else {
                result = getPlugDescriptionFromIndex(id);
            }
        }

        return result;
    }

    /**
     * Returns all registered IPlugs.
     *
     * @return All registered IPlugs without checking the time stamp.
     */
    public PlugDescription[] getAllIPlugsWithoutTimeLimitation() {
        Collection<PlugDescription> plugDescriptions;

        synchronized (this.fPlugDescriptionByPlugId) {
            plugDescriptions = this.fPlugDescriptionByPlugId.values();

            PlugDescription[] pdCopy = plugDescriptions.stream()
                    .map( pd -> {
                        PlugDescription pdCopy1 = new PlugDescription();
                        pdCopy1.putAll(pd);
                        return pdCopy1;
                    }).toArray(PlugDescription[]::new);

            for (PlugDescription pd : pdCopy) {
                if (ManagementService.MANAGEMENT_IPLUG_ID.equals(pd.getProxyServiceURL())
                        || SearchService.CENTRAL_INDEX_ID.equals(pd.getProxyServiceURL())) {
                    pd.remove("overrideProxy");
                }
            }
            return pdCopy;
        }
    }

    /**
     * Returns all IPlugs that are still alive and directly connected to iBus.
     *
     * @return All registered IPlugs younger than the given life time.
     */
    public PlugDescription[] getAllIPlugsConnected() {
        PlugDescription[] plugDescriptions = getAllIPlugsWithoutTimeLimitation();
        List<PlugDescription> plugs = new ArrayList<>(plugDescriptions.length);
        long now = System.currentTimeMillis();
        for (int i = 0; i < plugDescriptions.length; i++) {
            long plugLifeSign = plugDescriptions[i].getLong(LAST_LIFESIGN) + this.fLifeTime;
            if (plugLifeSign > now) {
                plugs.add(plugDescriptions[i]);
            }
        }
        return plugs.toArray(new PlugDescription[0]);
    }

    /**
     * Returns all IPlugs that are still alive.
     * Also generate those who indexed into central index.
     *
     * @return All registered IPlugs younger than the given life time.
     */
    public PlugDescription[] getAllIPlugs() {
        List<PlugDescription> plugs = new ArrayList<>();

        // check central index (ingrid_meta-index) and generate PlugDescriptions from
        // if an iPlug is not connected anymore to the iBus then we want the information from the
        // iPlug which once had indexed the data
        // TODO: iterate through index ingrid_meta and get all iPlug infos
        try {
            List<PlugDescription> plugDescriptionsFromIndex = getAllPlugDescriptionsFromIndex();

            // if it's a new installation with no indices at all, then return an empty result
            if (plugDescriptionsFromIndex == null) {
                return new PlugDescription[0];
            }

            for (PlugDescription plugDescription : plugDescriptionsFromIndex) {
                if (plugDescription.getProxyServiceURL() == null) continue;

                boolean pdAlreadyExists = plugs.stream().anyMatch(plug -> plug.getProxyServiceURL().equals(plugDescription.getProxyServiceURL()));

                // do not add same iPlug (one that is connected to iBus and one created from index)
                if (!pdAlreadyExists && plugDescription != null) {
                    plugs.add(plugDescription);
                }
            }
        } catch (NoNodeAvailableException ex) {
            fLogger.warn("Elasticsearch cluster not available");
        }

        // with a lower priority check if any connected iPlug should be used
        PlugDescription[] plugDescriptions = getAllIPlugsWithoutTimeLimitation();
        long now = System.currentTimeMillis();
        for (PlugDescription plugDescription : plugDescriptions) {
            long plugLifeSign = plugDescription.getLong(LAST_LIFESIGN) + this.fLifeTime;
            boolean pdAlreadyExists = plugs.stream().anyMatch(plug -> plug.getProxyServiceURL().equals(plugDescription.getProxyServiceURL()));

            // do not add same iPlug (one that is connected to iBus and one created from index)
            if (!pdAlreadyExists && plugLifeSign > now) {
                plugs.add(plugDescription);
            }
        }

        return plugs.toArray(new PlugDescription[0]);
    }

    /**
     * Removes all iPlugs that have timed out. The timeout is derived from
     * the last life sign of the iPlug + a 120 sec timeout.
     */
    public void removeIPlugsWithTimeout() {
        PlugDescription[] plugDescriptions = getAllIPlugsWithoutTimeLimitation();
        long now = System.currentTimeMillis();
        for (int i = 0; i < plugDescriptions.length; i++) {
            long plugLifeSign = plugDescriptions[i].getLong(LAST_LIFESIGN) + this.fLifeTime;
            if (plugLifeSign <= now) {
                fLogger.warn("remove iplug '"
                        + plugDescriptions[i].getPlugId()
                        + "' because last life sign is too old ("
                        + new Date(plugLifeSign) + " < " + new Date(now) + ")");
                removePlug(plugDescriptions[i].getPlugId());
                closeConnectionToIplug(plugDescriptions[i]);
            }
        }
    }


    /**
     * Returns a IPlug proxy to a given IPlug id.
     *
     * @param plugId A IPlug id to that the IPlug proxy should be returned.
     * @return The IPlug proxy.
     */
    public IPlug getPlugProxy(String plugId) {
        synchronized (this.fPlugProxyByPlugId) {
            if (iPlugsNotUsingCentralIndex.contains(plugId)) {
                return this.fPlugProxyByPlugId.get(plugId);
            } else {
                return this.fPlugProxyByPlugId.get(SearchService.CENTRAL_INDEX_ID);
            }
        }
    }

    /**
     * Only return the proxy to the real iPlug and not the central index.
     * @param plugId
     * @return
     */
    public IPlug getRealPlugProxy(String plugId) {
        synchronized (this.fPlugProxyByPlugId) {
            return this.fPlugProxyByPlugId.get(plugId);
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

            // create a sorted properties file
            this.fActivatedIplugs = new Properties() {
                private static final long serialVersionUID = 6956076060462348684L;

                @Override
                public synchronized Enumeration<Object> keys() {
                    return Collections.enumeration(new TreeSet<>(super.keySet()));
                }
            };

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
     * @param plugId A IPlug id from the IPlug that should be activated.
     * @throws IllegalArgumentException If the IPlug is unknown.
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
     * @param plugId A IPlug id from the IPlug that should be deactivated.
     * @throws IllegalArgumentException If the IPlugId is unknown.
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
     * @param communication The communication object to connect the IPlugs.
     */
    public void setCommunication(ICommunication communication) {
        this.fCommunication = communication;
    }

    /**
     * Sets the global ranking for all IPlugs.
     *
     * @param globalRanking A HashMap containing a boost factor to a IPlug id.
     */
    public void setGlobalRanking(HashMap<String, Float> globalRanking) {
        this.fGlobalRanking = globalRanking;
    }

    /**
     * Returns a ranking boost for a given IPlug id.
     *
     * @param plugId A boosting for IPlug id.
     * @return The boost factor to a IPlug.
     */
    public Float getGlobalRankingBoost(String plugId) {
        Float result = null;
        if (null != this.fGlobalRanking) {
            result = this.fGlobalRanking.get(plugId);
        }

        return result;
    }

    /**
     * Sets the bus url the IPlugs connected with.
     *
     * @param busurl The bus url. The form looks like /<group name>:<bus name>
     */
    public void setUrl(final String busurl) {
        this.fBusUrl = busurl;
    }

    private List<PlugDescription> getAllPlugDescriptionsFromIndex() {
        List<PlugDescription> plugDescriptions = new ArrayList<>();

        IPlug centralIPlug = this.getPlugProxy(SearchService.CENTRAL_INDEX_ID);
        IngridDocument response;
        IngridCall options = new IngridCall();
        options.setMethod("getAllIPlugInformation");
        try {
            response = centralIPlug.call(options);
        } catch (NoNodeAvailableException e) {
            fLogger.debug("No connection to Elasticsearch");
            return new ArrayList<>();
        } catch (Exception e) {
            fLogger.error("Could not get iPlug Info from ingrid_meta index", e);
            return null;
        }


        List<IngridDocument> infos = (List<IngridDocument>) response.get("iPlugInfos");

        for (IngridDocument info : infos) {
            PlugDescription pd = mapIPlugInfoToPlugdescription(info);
            if (pd != null) plugDescriptions.add(pd);
        }

        return plugDescriptions;
    }

    public PlugDescription getPlugDescriptionFromIndex(String plugId) {
        IPlug centralIPlug = this.getPlugProxy(SearchService.CENTRAL_INDEX_ID);
        IngridDocument response;
        IngridCall options = new IngridCall();
        options.setMethod("getIPlugInformation");
        options.setParameter(plugId);
        try {
            response = centralIPlug.call(options);
        } catch (Exception e) {
            fLogger.error("Could not get iPlug Info from ingrid_meta index", e);
            return null;
        }

        return mapIPlugInfoToPlugdescription(response);
    }

    private PlugDescription mapIPlugInfoToPlugdescription(IngridDocument info) {
        PlugDescription virtualPD = null;

        if (info != null && info.get("plugdescription") != null) {
            HashMap pdFromIndex = (HashMap) info.get("plugdescription");

            virtualPD = new PlugDescription();


            virtualPD.setProxyServiceURL((String) pdFromIndex.get("proxyServiceUrl"));

            List<String> datatypes = (List<String>) pdFromIndex.get("dataType");
            if (datatypes != null) {
                for (String datatype : datatypes) {
                    virtualPD.addDataType(datatype);
                }
            }

            List<String> fields = (List<String>) pdFromIndex.get("fields");
            if (fields != null) {
                for (String field : fields) {
                    virtualPD.addField(field);
                }
            }

            virtualPD.setDataSourceName((String) pdFromIndex.get("dataSourceName"));
            virtualPD.setDataSourceDescription((String) pdFromIndex.get("dataSourceDescription"));
            virtualPD.setIPlugClass((String) pdFromIndex.get("iPlugClass"));
            virtualPD.put("partner", pdFromIndex.get("partner"));
            virtualPD.put("provider", pdFromIndex.get("provider"));
            virtualPD.put("useRemoteElasticsearch", true);
            virtualPD.setRecordLoader(true);
            virtualPD.setRankinTypes(true, false, false);

            // virtualPD.put("createdFromIndex", true);
            virtualPD.activate();
        }
        return virtualPD;
    }

    public void addIPlugNotUsingCentralIndex(String id) {
        this.iPlugsNotUsingCentralIndex.add(id);
    }
}
