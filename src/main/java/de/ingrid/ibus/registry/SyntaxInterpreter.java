/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.tool.QueryUtil;
import de.ingrid.utils.tool.StringUtil;

/**
 * Supports you with static methods to extract various informations out of a query.
 */
public class SyntaxInterpreter {

    private static final Logger LOG = Logger.getLogger(SyntaxInterpreter.class);
    
    /**
     * Returns IPlugs to a given query. Currently it filters for activated, IPlug ids, supported ranking, supported
     * datatype, supported fields, supported providers and supported partners.
     * 
     * @param query The search query. 
     * @param registry The plug regestry.
     * @return The IPlugs that have the fields the query requires.
     */
    public static PlugDescription[] getIPlugsForQuery(IngridQuery query, Registry registry) {
        PlugDescription[] plugs = registry.getAllIPlugs();
        List<PlugDescription> plugList = new ArrayList<PlugDescription>(plugs.length);
        if(LOG.isDebugEnabled()) {
            LOG.debug("plugs before filtering");
        }
        for (int i = 0; i < plugs.length; i++) {
            if(LOG.isDebugEnabled()) {
                LOG.debug(i+ ".) " + plugs[i].getPlugId());
            }
            plugList.add(plugs[i]);
        }

        long ms = System.currentTimeMillis();
        filterActivatedIplugs(ms, plugList);
        filterForIPlugs(ms, query, plugList);
        filterForRanking(ms, query, plugList);
        filterForDataType(ms, query, plugList);
        filterForFields(ms, query, plugList);
        filterForProvider(ms, query, plugList);
        filterForPartner(ms, query, plugList);
        
        PlugDescription[] filteredPlugs = (PlugDescription[]) plugList.toArray(new PlugDescription[plugList.size()]);
        if(LOG.isDebugEnabled()) {
            LOG.debug("plugs after filtering");
            for (int j = 0; j < filteredPlugs.length; j++) {
                PlugDescription plugDescription = filteredPlugs[j];
                LOG.debug(j+ ".) " + plugDescription.getPlugId());    
            }
        }

        return filteredPlugs;
    }

    private static void filterActivatedIplugs(long ms, List<PlugDescription> plugDescriptions) {
        for (Iterator<PlugDescription> iter = plugDescriptions.iterator(); iter.hasNext();) {
            PlugDescription element = iter.next();
            if (!element.isActivate()) {
                if(LOG.isDebugEnabled()) {
                  LOG.debug(ms+ ": Not activated! Remove iplug: " + element.getProxyServiceURL());
                }
                iter.remove();
            }
        }
    }

    private static void filterForRanking(long ms, IngridQuery ingridQuery, List<PlugDescription> descriptions) {
        String rankingTypeInQuery = ingridQuery.getRankingType();
        if (rankingTypeInQuery == null) {
            ingridQuery.put(IngridQuery.RANKED, IngridQuery.NOT_RANKED);
            rankingTypeInQuery = ingridQuery.getRankingType();
        }
        if(LOG.isDebugEnabled()) {
            LOG.debug("rankingType in Query: " + rankingTypeInQuery);
        }
        if (!rankingTypeInQuery.equals("any")) {
            for (Iterator<PlugDescription> iter = descriptions.iterator(); iter.hasNext();) {
                PlugDescription plugDescription = iter.next();
                String[] rankingTypes = plugDescription.getRankingTypes();
                if(LOG.isDebugEnabled()) {
                    LOG.debug("plugdescription/rankingTypes.length: " + plugDescription.getPlugId() + " / " + rankingTypes.length);
                }
                boolean foundRanking = false;
                for (int i = 0; i < rankingTypes.length; i++) {
                    if(LOG.isDebugEnabled()) {
                        LOG.debug("rankingType in plugdescription: " + rankingTypes[i] + " / " + plugDescription.getPlugId());
                    }
                    if (ingridQuery.isRanked(rankingTypes[i].toLowerCase())) {
                        foundRanking = true;
                        break;
                    }
                }
                if (!foundRanking && rankingTypes.length > 0) {
                    if(LOG.isDebugEnabled()) {
                        LOG.debug(ms + " remove plugescription: " + plugDescription.getPlugId());
                    }
                    iter.remove();
                }
            }
        }
    }

    private static void filterForFields(long ms, IngridQuery ingridQueries, List<PlugDescription> allIPlugs) {
        String[] queryFieldNames = getAllFieldsNamesFromQuery(ingridQueries);

        // Query may contain "metainfo" field not supported by all iplugs !
        // Remove "metainfo" field from field list for checking ! So old iplugs won't be removed !
    	queryFieldNames = StringUtil.removeStringFromStringArray(queryFieldNames, QueryUtil.FIELDNAME_METAINFO);

        if (queryFieldNames.length == 0) {
            return;
        }

        for (Iterator<PlugDescription> iter = allIPlugs.iterator(); iter.hasNext();) {
            PlugDescription plugDescription = iter.next();
            String[] plugFields = plugDescription.getFields();
            boolean toRemove = true;
            for (int i = 0; i < plugFields.length; i++) {
                if (containsString(queryFieldNames, plugFields[i])) {
                    toRemove = false;
                    break;
                }
            }
            if (toRemove) {
                if(LOG.isDebugEnabled()) {
                  LOG.debug(ms+" remove iplug: " + plugDescription.getProxyServiceURL());
                  LOG.debug(ms+" queryFieldNames: " + Arrays.asList(queryFieldNames));
                  LOG.debug(ms+" plugfields: " + Arrays.asList(plugFields));
                }
                iter.remove();
            }
        }
    }

    private static void filterForDataType(long ms, IngridQuery ingridQueries, List<PlugDescription> allIPlugs) {
        String[] allowedDataTypes = ingridQueries.getPositiveDataTypes();
        String[] notAllowedDataTypes = ingridQueries.getNegativeDataTypes();
        if (allowedDataTypes.length == 0 && notAllowedDataTypes.length == 0) {
            return;
        }

        if (!containsString(allowedDataTypes, "any")) {
            for (Iterator<PlugDescription> iter = allIPlugs.iterator(); iter.hasNext();) {
                PlugDescription plugDescription = iter.next();
                String[] dataTypes = plugDescription.getDataTypes();
                // if only negative datatypes are supplied, exclude only iplugs with negative datatype and keep others
                // if positive datatypes are supplied, exclude all except those with the positive datatype
                boolean toRemove = allowedDataTypes.length == 0 ? false : true;
                for (int i = 0; i < dataTypes.length; i++) {
                    if (containsString(notAllowedDataTypes, dataTypes[i]) || containsString(notAllowedDataTypes, "all")) {
                        toRemove = true;
                        break;
                    }
                    if (containsString(allowedDataTypes, dataTypes[i])) {
                        toRemove = false;
                    }
                }
                if (toRemove) {
                    if(LOG.isDebugEnabled()) {
                      LOG.debug(ms+" remove iplug: " + plugDescription.getProxyServiceURL());
                    }
                    iter.remove();
                }
            }
        }
    }

    private static void filterForProvider(long ms, IngridQuery ingridQueries, List<PlugDescription> allIPlugs) {
        String[] allowedProvider = ingridQueries.getPositiveProvider();
        String[] notAllowedProvider = ingridQueries.getNegativeProvider();
        if (allowedProvider.length == 0 && notAllowedProvider.length == 0) {
            return;
        }
        for (Iterator<PlugDescription> iter = allIPlugs.iterator(); iter.hasNext();) {
            PlugDescription plugDescription = iter.next();
            
            // FIX: INGRID-1463
            String iPlugClass = plugDescription.getIPlugClass(); 
            if ((null != iPlugClass) && (iPlugClass.equals("de.ingrid.iplug.se.NutchSearcher"))) {
                continue;
            }
            //
            
            String[] providers = plugDescription.getProviders();
            boolean toRemove = true;
            if (allowedProvider.length == 0) {
                toRemove = false;
            }
            for (int i = 0; i < providers.length; i++) {
                if (containsString(notAllowedProvider, providers[i])) {
                    toRemove = true;
                    break;
                }
                if (containsString(allowedProvider, providers[i])) {
                    toRemove = false;
                }
            }
            if (toRemove) {
                if(LOG.isDebugEnabled()) {
                  LOG.debug(ms+" remove iplug: " + plugDescription.getProxyServiceURL());
                }
                iter.remove();
            }
        }
    }

    private static void filterForPartner(long ms, IngridQuery ingridQuery, List<PlugDescription> allIPlugs) {
        String[] allowedPartner = ingridQuery.getPositivePartner();
        String[] notAllowedPartner = ingridQuery.getNegativePartner();

        if (allowedPartner.length == 0 && notAllowedPartner.length == 0) {
            return;
        }

        for (Iterator<PlugDescription> iter = allIPlugs.iterator(); iter.hasNext();) {
            PlugDescription plugDescription = iter.next();
            
            // FIX: INGRID-1463
            String iPlugClass = plugDescription.getIPlugClass(); 
            if ((null != iPlugClass) && (iPlugClass.equals("de.ingrid.iplug.se.NutchSearcher"))) {
                continue;
            }
            //
            
            String[] partners = plugDescription.getPartners();
            boolean toRemove = true;
            if (allowedPartner.length == 0) {
                toRemove = false;
            }
            for (int i = 0; i < partners.length; i++) {
                if (containsString(notAllowedPartner, partners[i])) {
                    toRemove = true;
                    break;
                }
                if (containsString(allowedPartner, partners[i])) {
                    toRemove = false;
                }
            }
            if (toRemove) {
                if(LOG.isDebugEnabled()) {
                  LOG.debug(ms+" remove iplug: " + plugDescription.getProxyServiceURL());
                }
                iter.remove();
            }
        }
    }

    private static void filterForIPlugs(long ms, IngridQuery query, List<PlugDescription> plugs) {
        String[] restrictecPlugIds = query.getIPlugs();
        if (restrictecPlugIds.length == 0) {
            return;
        }
        for (Iterator<PlugDescription> iter = plugs.iterator(); iter.hasNext();) {
            PlugDescription plugDescription = iter.next();
            if (!containsString(restrictecPlugIds, plugDescription.getPlugId())) {
                if(LOG.isDebugEnabled()) {
                  LOG.debug(ms+" remove iplug: " + plugDescription.getProxyServiceURL());
                }
                iter.remove();
            }
        }
    }

    /**
     * @param query
     * @return all fields of a given query and subqueries
     */
    private static String[] getAllFieldsNamesFromQuery(IngridQuery query) {
        ArrayList<String> fieldsList = new ArrayList<String>();
        QueryUtil.getFieldNamesFromQuery(query, fieldsList);
        return (String[]) fieldsList.toArray(new String[fieldsList.size()]);
    }

    private static boolean containsString(String[] allowedDataTypes, String oneType) {
        for (int i = 0; i < allowedDataTypes.length; i++) {
            if (allowedDataTypes[i].equalsIgnoreCase(oneType)) {
                return true;
            }
        }
        return false;
    }

}
