/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: DispatcherTest.java,v $
 */
package de.ingrid.ibus.v01;
import de.ingrid.ibus.registry.RegistryTest;
import de.ingrid.ibus.registry.SyntaxInterpreterTest;
import junit.framework.TestCase;

/**
 * Test for iplug-registry-feature (INGRID-5).
 * created on 21.07.2005 <p>
 *
 * @author hs
 */
public class IPlugRegistryTest extends TestCase {
    /**
     * 
     */
    public void testParseMetaData() {
        fail("INGRID-28 not yet implemented");
    }
    /**
     * 
     */
    public void testStoreConnectionSettingsLocally() {
        fail("INGRID-29 not yet implemented");
    }
    /**
     * @throws Exception 
     * 
     */
    public void testStoreIndexStructures() throws Exception {
        new RegistryTest().testAddAndGet();
        new SyntaxInterpreterTest().testGetIplugsForQuery();
        
        
    }
    /**
     * 
     */
    public void testStoreIndexStructuresTemporary() {
        fail("INGRID-31 not yet implemented");
    }
    /**
     * test for INGRID-32
     * 
     * @throws Exception 
     * 
     */
    public void testStoreDataInRAM() throws Exception {
        new RegistryTest().testAddAndGet();
    }
    /**
     * 
     */
    public void testSearchAPI() {
        fail("INGRID-33 not yet implemented");
    }

}
