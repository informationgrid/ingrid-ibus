/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.query.ClauseQuery;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.query.TermQuery;

/**
 * Supports you with static methods to extract various informations out of a
 * query.
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
        
        PlugDescription[] allIPlugs = getActivatedIplugs(registry.getAllIPlugsWithoutTimeLimitation()); // FIXME uses deprcated method.
        String[] posDataTypes = query.getPositiveDataTypes();
        String[] negDataTypes = query.getNegativeDataTypes();
        boolean hasTerms = queryHasTerms(query);
     
        if (hasTerms && posDataTypes.length == 0 && negDataTypes.length ==0) {
            return filterForRanking(query,allIPlugs);
        }

        String[] fields = getAllFieldsFromQuery(query);
        if ((posDataTypes.length > 0 || negDataTypes.length>0)&& fields.length > 0) {
            return   filterForRanking(query,filterForDataTypeAndFields(allIPlugs, posDataTypes, negDataTypes, fields));
        }
        if (posDataTypes.length == 0 && negDataTypes.length == 0 && fields.length > 0) {
        	 return filterForRanking(query,filterForFields(allIPlugs, fields));
        }
        if (posDataTypes.length > 0 || negDataTypes.length > 0) {
             return filterForRanking(query, filterForDataType(allIPlugs, posDataTypes, negDataTypes));
        }
        return new PlugDescription[0];
    }

    private static PlugDescription[] getActivatedIplugs(PlugDescription[] descriptions) {
		ArrayList arrayList = new ArrayList();
		for (int i = 0; i < descriptions.length; i++) {
			if (descriptions[i].isActivate()) {
				arrayList.add(descriptions[i]);
			}
		}
		return (PlugDescription[]) arrayList
				.toArray(new PlugDescription[arrayList.size()]);
	}
    
    private static PlugDescription[] filterForRanking(IngridQuery ingridQuery,
			PlugDescription[] descriptions) {

        if (ingridQuery.getRankingType() == null) {
            ingridQuery.put(IngridQuery.RANKED, IngridQuery.NOT_RANKED);
        }
		ArrayList arrayList = new ArrayList();

		for (int i = 0; i < descriptions.length; i++) {
			String[] rankingTypes = descriptions[i].getRankingTypes();
			if (rankingTypes.length > 0) {
				for (int j = 0; j < rankingTypes.length; j++) {
					if (ingridQuery.isRanked(rankingTypes[j].toLowerCase())) {
						arrayList.add(descriptions[i]);
						break;
					}
				}
			} else {
				arrayList.add(descriptions[i]);
			}
		}

		return (PlugDescription[]) arrayList
				.toArray(new PlugDescription[arrayList.size()]);

	}
    /**
     * @param allIPlugs
     * @param fields
     * @return plugs have at least one matching field
     */
    private static PlugDescription[] filterForFields(PlugDescription[] allIPlugs, String[] fields) {
        ArrayList arrayList = new ArrayList();
        HashSet hashSet = new HashSet();
        hashSet.addAll(Arrays.asList(fields));
        for (int i = 0; i < allIPlugs.length; i++) {
            PlugDescription plug = allIPlugs[i];
            String[] plugFields = plug.getFields();
            for (int j = 0; j < plugFields.length; j++) {
                String field = plugFields[j].toLowerCase();
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
     * @param allowedDataTypes
     * @param fields
     * @return plugs matching datatype and have at least one matching field
     */
    private static PlugDescription[] filterForDataTypeAndFields(PlugDescription[] allIPlugs, String[] allowedDataTypes, String[] notAllowedDatatypes,
            String[] fields) {
        ArrayList arrayList = new ArrayList();
        HashSet requiredFields = new HashSet();
        requiredFields.addAll(Arrays.asList(fields));
        for (int i = 0; i < allIPlugs.length; i++) {
            PlugDescription plug = allIPlugs[i];
            String[] dataTypes = plug.getDataTypes();
            boolean added = false;
            for (int j = 0; j < dataTypes.length && !added; j++) {
                String oneType = dataTypes[j];
                if (containsOneDataType(allowedDataTypes, oneType) && !containsOneDataType(notAllowedDatatypes, oneType)) {
                    String[] plugFields = plug.getFields();
                    for (int k = 0; k < plugFields.length; k++) {
                        String field = plugFields[k].toLowerCase();
                        if (requiredFields.contains(field)) {
                            arrayList.add(plug);
                            added = true;
                            break; // we need if only once of the fields occures
                        }
                    }

                }
            }
            
           
        }
        return (PlugDescription[]) arrayList.toArray(new PlugDescription[arrayList.size()]);
    }

    private static boolean containsOneDataType(String[] allowedDataTypes, String oneType) {
        for (int i = 0; i < allowedDataTypes.length; i++) {
            if (allowedDataTypes[i].equals(oneType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param allIPlugs
     * @param allowedDataTypes
     * @return only plugs matching given datatype.
     */
    private static PlugDescription[] filterForDataType(PlugDescription[] allIPlugs, String[] allowedDataTypes, String[] notAllowedDataTypes) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < allIPlugs.length; i++) {
            PlugDescription plug = allIPlugs[i];
            String[] dataTypes = plug.getDataTypes();
            for (int j = 0; j < dataTypes.length; j++) {
                String oneType = dataTypes[j];
                if (containsOneDataType(allowedDataTypes, oneType) && !containsOneDataType(notAllowedDataTypes, oneType)) {
                    arrayList.add(plug);
                    break; // if this plug suuport at least one type we add it to the list.
                }    
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
