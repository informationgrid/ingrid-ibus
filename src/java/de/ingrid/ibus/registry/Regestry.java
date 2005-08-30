/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import de.ingrid.iplug.IIPlug;
import de.ingrid.utils.IngridQuey;

/**
 * 
 * created on 21.07.2005 <p>
 *
 * @author hs
 */

public class Regestry {
    private Hashtable fRegisteredIPlugs = new Hashtable();
    
    /**
     * Registers the given <code>IIPlug</code> instance.
     * @param plug The <code>IIPlug</code> instance to register.
     */
    public void addIPlug(IIPlug plug) {
        this.fRegisteredIPlugs.put(plug.getId(), plug);
    }

    /**
     * Allows access to all registered <code>IIplug</code> instances. 
     * @param id The id of the registered <code>IIPlug</code> instance.
     * @return null if no <code>IIPlug</code> instance is registered.
     */
    public IIPlug getIPlug(String id) {
        return (IIPlug) this.fRegisteredIPlugs.get(id);
    }

    /**
     * 
     * @param query
     * @return All registered <code>IIPlug</code> instances which supports data fields which in the given query contains.       
     */
    public IIPlug[] getIPlugsForQuery(IngridQuey query) {
        final List iPlugList = new ArrayList();
        Iterator iterator = this.fRegisteredIPlugs.values().iterator();
        IIPlug plug = null;
        while (iterator.hasNext()) {
            plug = (IIPlug) iterator.next();
            if(checkForDataFields(query, plug.getFields())){
                iPlugList.add(plug);
            }
        }
        return (IIPlug[]) iPlugList.toArray(new IIPlug[iPlugList.size()]);
    }
    
    
    /**
     * Checks for occurrence of datafields in the query. 
     * @param ingridQuery The query to check for coccurrence of the given datafields.
     * @param dataFields The fields to check for occurrence in the given query.
     * @return true if one of the datafields occurs in the given query.  
     */
    private static boolean checkForDataFields(IngridQuey ingridQuery, String[] dataFields){
        final Enumeration fields = ingridQuery.getFields();
        final List dataFieldList = Arrays.asList(dataFields);
        while (fields.hasMoreElements()) {
            if(dataFieldList.contains(fields.nextElement())){
                return true;
            }
        }
        return false;
    }

}
