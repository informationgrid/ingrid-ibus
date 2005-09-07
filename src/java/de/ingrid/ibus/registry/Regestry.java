/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.registry;

import java.util.ArrayList;
import java.util.HashMap;

import de.ingrid.iplug.IIPlug;
import de.ingrid.utils.ClauseQuery;
import de.ingrid.utils.FieldQuery;
import de.ingrid.utils.IngridQuery;
import de.ingrid.utils.TermQuery;

/**
 * 
 * created on 21.07.2005
 * <p>
 * 
 * @author hs
 */

public class Regestry {

    private HashMap fCache = new HashMap();

    private HashMap fFieldIndex = new HashMap();

    /**
     * Adds a iplug to the registry
     * 
     * @param plug
     */
    public void addIPlug(IIPlug plug) {
        putToCache(plug);
    }

    private void putToCache(IIPlug plug) {
        this.fCache.put(plug.getId(), plug);
        String[] fields = plug.getFields();
        if (fields != null) {
            for (int i = 0; i < fields.length; i++) {
                String field = fields[i];
                ArrayList list = (ArrayList) this.fFieldIndex.get(field);
                if (list == null) {
                    list = new ArrayList();
                    this.fFieldIndex.put(field, list);
                }
                list.add(plug);
            }
        }

    }

    /**
     * @param id
     * @return the iplug by key or <code>null</code>
     */
    public IIPlug getIPlug(String id) {
        return (IIPlug) this.fCache.get(id);
    }

    /**
     * @param query
     * @return the iiplugs that have the fields the query require.
     */
    public IIPlug[] getIPlugsForQuery(IngridQuery query) {

        if (queryHasTerms(query)) {
            return (IIPlug[]) this.fCache.values().toArray(new IIPlug[this.fCache.size()]);
        }

        ArrayList fields = new ArrayList();
        getFieldsFromQuery(query, fields);
        ArrayList plugs = new ArrayList();
        int fieldCount = fields.size();
        for (int i = 0; i < fieldCount; i++) {
            String fieldName = (String) fields.get(i);
            ArrayList list = (ArrayList) this.fFieldIndex.get(fieldName);
            if (list != null) {
                plugs.addAll(list);
            }
        }

        return (IIPlug[]) plugs.toArray(new IIPlug[plugs.size()]);
    }

    private boolean queryHasTerms(IngridQuery query) {
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
    private void getFieldsFromQuery(IngridQuery query, ArrayList fieldList) {
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
