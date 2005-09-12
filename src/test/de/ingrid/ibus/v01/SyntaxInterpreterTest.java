/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: DispatcherTest.java,v $
 */
package de.ingrid.ibus.v01;
import junit.framework.TestCase;

/**
 * Test for syntax-interpreter-feature (INGRID-1).
 * created on 21.07.2005 <p>
 *
 * @author hs
 */
public class SyntaxInterpreterTest extends TestCase {
    private static de.ingrid.ibus.registry.SyntaxInterpreterTest interpreterTest = new de.ingrid.ibus.registry.SyntaxInterpreterTest();
    
    /**
     * test for feature INGRID-36
     * @throws Exception 
     */
    public void testTranslateToQuerySyntax() throws Exception {
       interpreterTest.testParseQuery(); 
    }
    /**
     * test for feature INGRID-37  
     * @throws Exception 
     */
    public void testFindAddressedDataSources() throws Exception{
        interpreterTest.testGetIplugsForQuery();
    }
    /**
     * test for feature INGRID-38
     * @throws Exception 
     */
    public void testFindDataFields() throws Exception {
        interpreterTest.testGetIplugsForQuery();
    }
}
