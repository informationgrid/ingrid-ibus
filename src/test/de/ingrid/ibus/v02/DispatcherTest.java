/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: DispatcherTest.java,v $
 */

package de.ingrid.ibus.v02;

import junit.framework.TestCase;
import de.ingrid.ibus.BusTest;
import de.ingrid.ibus.registry.SyntaxInterpreterTest;

/**
 * Test for dispatcher feature (INGRID-2)
 * 
 * <p/> created on 21.07.2005
 * 
 * 
 * @author hs
 */
public class DispatcherTest extends TestCase {
    
    /**
     * INGRID-18
     * @throws Exception 
     */
    public void testRedirectQueries() throws Exception {
        //for dispatching ability
        new SyntaxInterpreterTest().testGetIPlugs_DataTypes();
        new SyntaxInterpreterTest().testGetIplugs_Fields();
        new SyntaxInterpreterTest().testGetIplugs_FieldsAndDataTypes();
        
        //for integration
        new BusTest().testSearch();
    }

    /**
     * INGRID-19
     * @throws Exception 
     */
    public void testReturnOfResults() throws Exception {
        new BusTest().testSearch();
    }

    /**
     * INGRID-20
     * @throws Exception 
     * @throws Exception 
     */
    public void testCommunicationWithStatisticOfAccesses() throws Exception{
        new BusTest().testSearchWithStatisticPreProcessor();
        new BusTest().testSearchWithStatisticPostProcessor();
    }
}
