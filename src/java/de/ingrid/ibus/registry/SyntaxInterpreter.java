/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import de.ingrid.iplug.IIPlug;
import de.ingrid.utils.ClauseQuery;
import de.ingrid.utils.FieldQuery;
import de.ingrid.utils.IngridQuery;
import de.ingrid.utils.TermQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;

public class SyntaxInterpreter {
    
    /**
     * Passes the given query to the {@link QueryStringParser}
     * @param query The query to parse
     * @return The parsed query object
     * @throws ParseException
     */
    
    public static IngridQuery parseQuery(String query) throws ParseException{
        return QueryStringParser.parse(query);
    }
    
    
    /**
     * @param query
     * @return the iplugs that have the fields the query require.
     */
    public static IIPlug[] getIPlugsForQuery(IngridQuery query, Regestry regestry) {
    
        String dataType = query.getDataType();
        IIPlug[] allIPlugs = regestry.getAllIPlugs();
        boolean hasTerms = queryHasTerms(query);
        if (hasTerms && dataType != null) {
            return filterForDataType(allIPlugs, dataType);
        }
        String[] fields = getAllFieldsFromQuery(query);
    
        if (dataType == null && fields.length == 0 && hasTerms) {
            return allIPlugs;
        }
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
    private static IIPlug[] filterForFields(IIPlug[] allIPlugs, String[] fields) {
        ArrayList arrayList = new ArrayList();
        HashSet hashSet = new HashSet();
        hashSet.addAll(Arrays.asList(fields));
        for (int i = 0; i < allIPlugs.length; i++) {
            IIPlug plug = allIPlugs[i];
            String[] plugFields = plug.getFields();
            for (int j = 0; j < plugFields.length; j++) {
                String field = plugFields[j];
                if (hashSet.contains(field)) {
                    arrayList.add(plug);
                    break;
                }
            }
    
        }
        return (IIPlug[]) arrayList.toArray(new IIPlug[arrayList.size()]);
    }

    /**
     * @param allIPlugs
     * @param dataType
     * @param fields
     * @return plugs matching datatype and have at least one matching field
     */
    private static IIPlug[] filterForDataTypeAndFields(IIPlug[] allIPlugs, String dataType, String[] fields) {
        ArrayList arrayList = new ArrayList();
        HashSet requiredFields = new HashSet();
        requiredFields.addAll(Arrays.asList(fields));
        for (int i = 0; i < allIPlugs.length; i++) {
            IIPlug plug = allIPlugs[i];
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
        return (IIPlug[]) arrayList.toArray(new IIPlug[arrayList.size()]);
    }

    /**
     * @param allIPlugs
     * @param dataType
     * @return only plugs matching given datatype.
     */
    private static IIPlug[] filterForDataType(IIPlug[] allIPlugs, String dataType) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < allIPlugs.length; i++) {
            IIPlug plug = allIPlugs[i];
            if (plug.getDataType().equals(dataType)) {
                arrayList.add(plug);
            }
        }
        return (IIPlug[]) arrayList.toArray(new IIPlug[arrayList.size()]);
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
