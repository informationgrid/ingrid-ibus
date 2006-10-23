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

import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.query.ClauseQuery;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;

/**
 * Supports you with static methods to extract various informations out of a query.
 * 
 * <p/>created on 19.10.2005
 * 
 * @version $Revision: $
 * @author sg
 * 
 */
public class SyntaxInterpreter {

    /**
     * @param query
     * @param registry
     * @return the iplugs that have the fields the query require.
     */
    public static PlugDescription[] getIPlugsForQuery(IngridQuery query, Registry registry) {
        PlugDescription[] plugs = registry.getAllIPlugs();
        List plugList = new ArrayList(plugs.length);
        for (int i = 0; i < plugs.length; i++) {
            plugList.add(plugs[i]);
        }

        filterActivatedIplugs(plugList);
        filterForIPlugs(query, plugList);
        filterForRanking(query, plugList);
        filterForDataType(query, plugList);
        filterForFields(query, plugList);
        filterForProvider(query, plugList);
        filterForPartner(query, plugList);

        return (PlugDescription[]) plugList.toArray(new PlugDescription[plugList.size()]);
    }

    private static void filterActivatedIplugs(List plugDescriptions) {
        for (Iterator iter = plugDescriptions.iterator(); iter.hasNext();) {
            PlugDescription element = (PlugDescription) iter.next();
            if (!element.isActivate()) {
                iter.remove();
            }
        }
    }

    private static void filterForRanking(IngridQuery ingridQuery, List descriptions) {
        String rankingTypeInQuery = ingridQuery.getRankingType();
        if (rankingTypeInQuery == null) {
            ingridQuery.put(IngridQuery.RANKED, IngridQuery.NOT_RANKED);
        }

        if ((rankingTypeInQuery == null) || !rankingTypeInQuery.equals("any")) {
            for (Iterator iter = descriptions.iterator(); iter.hasNext();) {
                PlugDescription plugDescription = (PlugDescription) iter.next();
                String[] rankingTypes = plugDescription.getRankingTypes();
                boolean foundRanking = false;
                for (int i = 0; i < rankingTypes.length; i++) {
                    if (ingridQuery.isRanked(rankingTypes[i].toLowerCase())) {
                        foundRanking = true;
                        break;
                    }
                }
                if (!foundRanking && rankingTypes.length > 0) {
                    iter.remove();
                }
            }
        }
    }

    private static void filterForFields(IngridQuery ingridQueries, List allIPlugs) {
        String[] queryFieldNames = getAllFieldsNamesFromQuery(ingridQueries);
        if (queryFieldNames.length == 0) {
            return;
        }

        for (Iterator iter = allIPlugs.iterator(); iter.hasNext();) {
            PlugDescription plugDescription = (PlugDescription) iter.next();
            String[] plugFields = plugDescription.getFields();
            boolean toRemove = true;
            for (int i = 0; i < plugFields.length; i++) {
                if (containsString(queryFieldNames, plugFields[i])) {
                    toRemove = false;
                    break;
                }
            }
            if (toRemove) {
                iter.remove();
            }
        }
    }

    private static void filterForDataType(IngridQuery ingridQueries, List allIPlugs) {
        String[] allowedDataTypes = ingridQueries.getPositiveDataTypes();
        String[] notAllowedDataTypes = ingridQueries.getNegativeDataTypes();
        if (allowedDataTypes.length == 0 && notAllowedDataTypes.length == 0) {
            return;
        }

        if (!containsString(allowedDataTypes, "any")) {
            for (Iterator iter = allIPlugs.iterator(); iter.hasNext();) {
                PlugDescription plugDescription = (PlugDescription) iter.next();
                String[] dataTypes = plugDescription.getDataTypes();
                boolean toRemove = true;
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
                    iter.remove();
                }
            }
        }
    }

    private static void filterForProvider(IngridQuery ingridQueries, List allIPlugs) {
        String[] allowedProvider = ingridQueries.getPositiveProvider();
        String[] notAllowedProvider = ingridQueries.getNegativeProvider();
        if (allowedProvider.length == 0 && notAllowedProvider.length == 0) {
            return;
        }

        for (Iterator iter = allIPlugs.iterator(); iter.hasNext();) {
            PlugDescription plugDescription = (PlugDescription) iter.next();
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
                iter.remove();
            }
        }

    }

    private static void filterForPartner(IngridQuery ingridQuery, List allIPlugs) {
        String[] allowedPartner = ingridQuery.getPositivePartner();
        String[] notAllowedPartner = ingridQuery.getNegativePartner();

        if (allowedPartner.length == 0 && notAllowedPartner.length == 0) {
            return;
        }

        for (Iterator iter = allIPlugs.iterator(); iter.hasNext();) {
            PlugDescription plugDescription = (PlugDescription) iter.next();
            String[] partners = plugDescription.getPartners();
            boolean toRemove = true;
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
                iter.remove();
            }
        }
    }

    private static void filterForIPlugs(IngridQuery query, List plugs) {
        String[] restrictecPlugIds = query.getIPlugs();
        if (restrictecPlugIds.length == 0) {
            return;
        }
        for (Iterator iter = plugs.iterator(); iter.hasNext();) {
            PlugDescription plugDescription = (PlugDescription) iter.next();
            if (!containsString(restrictecPlugIds, plugDescription.getPlugId())) {
                iter.remove();
            }
        }
    }

    /**
     * @param query
     * @return all fields of a given query and subqueries
     */
    private static String[] getAllFieldsNamesFromQuery(IngridQuery query) {
        ArrayList fieldsList = new ArrayList();
        getFieldNamesFromQuery(query, fieldsList);
        return (String[]) fieldsList.toArray(new String[fieldsList.size()]);
    }

    /**
     * Recursive loop to extract field names from queries and clause subqueries
     * 
     * @param query
     * @param fieldList
     */
    private static void getFieldNamesFromQuery(IngridQuery query, ArrayList fieldList) {
        FieldQuery[] fields = query.getFields();
        for (int i = 0; i < fields.length; i++) {
            fieldList.add(fields[i].getFieldName());
        }
        ClauseQuery[] clauses = query.getClauses();
        for (int i = 0; i < clauses.length; i++) {
            getFieldNamesFromQuery(clauses[i], fieldList);
        }
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
