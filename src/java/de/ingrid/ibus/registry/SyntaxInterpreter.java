/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import de.ingrid.iplug.PlugDescription;
import de.ingrid.utils.query.ClauseQuery;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.query.TermQuery;

public class SyntaxInterpreter {

    /**
     * @param query
     * @return the iplugs that have the fields the query require.
     */
    public static PlugDescription[] getIPlugsForQuery(IngridQuery query, Regestry regestry) {
    
        String dataType = query.getDataType();
        PlugDescription[] allIPlugs = regestry.getAllIPlugs();
        boolean hasTerms = queryHasTerms(query);
        if (hasTerms && dataType != null) {
            return filterForDataType(allIPlugs, dataType);
        }
        if (dataType == null && hasTerms) {
            return allIPlugs;
        }
        
        String[] fields = getAllFieldsFromQuery(query);
        
        if (dataType != null) {
            return filterForDataTypeAndFields(allIPlugs, dataType, fields);
        }
        if (dataType == null && fields.length > 0) {
            return filterForFields(allIPlugs, fields);
        }
        return null;
    
    }

    /**
     * @param allIPlugs
     * @param fields
     * @return plugs have at least one matching field
     */
    public static PlugDescription[] filterForFields(PlugDescription[] allIPlugs, String[] fields) {
        ArrayList arrayList = new ArrayList();
        HashSet hashSet = new HashSet();
        hashSet.addAll(Arrays.asList(fields));
        for (int i = 0; i < allIPlugs.length; i++) {
            PlugDescription plug = allIPlugs[i];
            String[] plugFields = plug.getFields();
            for (int j = 0; j < plugFields.length; j++) {
                String field = plugFields[j];
                if (hashSet.contains(field)) {
                    arrayList.add(plug);
                    break;
                }
            }
    
        }
        return (PlugDescription[]) arrayList.toArray(new PlugDescription[arrayList.size()]);
    }

    /**
     * @param allIPlugs
     * @param dataType
     * @param fields
     * @return plugs matching datatype and have at least one matching field
     */
    public static PlugDescription[] filterForDataTypeAndFields(PlugDescription[] allIPlugs, String dataType, String[] fields) {
        ArrayList arrayList = new ArrayList();
        HashSet requiredFields = new HashSet();
        requiredFields.addAll(Arrays.asList(fields));
        for (int i = 0; i < allIPlugs.length; i++) {
            PlugDescription plug = allIPlugs[i];
            if (plug.getDataType().equals(dataType)) {
                String[] plugFields = plug.getFields();
                for (int j = 0; j < plugFields.length; j++) {
                    String field = plugFields[j];
                    if (requiredFields.contains(field)) {
                        arrayList.add(plug);
                        break; // we need if only once of the fields occures
                    }
                }
    
            }
        }
        return (PlugDescription[]) arrayList.toArray(new PlugDescription[arrayList.size()]);
    }

    /**
     * @param allIPlugs
     * @param dataType
     * @return only plugs matching given datatype.
     */
    public static PlugDescription[] filterForDataType(PlugDescription[] allIPlugs, String dataType) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < allIPlugs.length; i++) {
            PlugDescription plug = allIPlugs[i];
            if (plug.getDataType().equals(dataType)) {
                arrayList.add(plug);
            }
        }
        return (PlugDescription[]) arrayList.toArray(new PlugDescription[arrayList.size()]);
    }

    /**
     * @param query
     * @return all fields of a given query and subqueries
     */
    private static String[] getAllFieldsFromQuery(IngridQuery query) {
        ArrayList fieldsList = new ArrayList();
        getFieldsFromQuery(query, fieldsList);
        return (String[]) fieldsList.toArray(new String[fieldsList.size()]);
    }

    private static boolean queryHasTerms(IngridQuery query) {
        TermQuery[] terms = query.getTerms();
        if (terms.length > 0) {
            return true;
        }
        ClauseQuery[] clauses = query.getClauses();
        for (int i = 0; i < clauses.length; i++) {
            if (queryHasTerms(clauses[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Recursive loop to extract field names from queries and clause subqueries
     * 
     * @param query
     * @param fieldList
     */
    private static void getFieldsFromQuery(IngridQuery query, ArrayList fieldList) {
        FieldQuery[] fields = query.getFields();
        for (int i = 0; i < fields.length; i++) {
            fieldList.add(fields[i].getFieldName());
        }
        ClauseQuery[] clauses = query.getClauses();
        for (int i = 0; i < clauses.length; i++) {
            getFieldsFromQuery(clauses[i], fieldList);
        }
    }

}
