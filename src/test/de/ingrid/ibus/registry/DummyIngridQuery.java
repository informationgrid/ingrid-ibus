/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: DispatcherTest.java,v ${date}
 */
package de.ingrid.ibus.registry;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

import de.ingrid.utils.IngridQuey;

/**
 * created on 21.07.2005 <p>
 *
 * @author hs
 */

public class DummyIngridQuery extends IngridQuey {
    private String[] fFields = new String[0];  
    
    /**
     * @param arg0
     * @param arg1
     */
    public DummyIngridQuery(long arg0, String arg1) {
        super(arg0, arg1);
    }
    
    /**
     * @param fields
     */
    public void setFields(String[] fields){
        this.fFields = fields;
    }
    
    /**
     * 
     * @return fields as enumerator.
     */
    public Enumeration getFields() {
        return Collections.enumeration(Arrays.asList(this.fFields));
    }
}
