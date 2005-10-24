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
 * Test for iplug-registry-feature (INGRID-5). created on 21.07.2005
 * <p>
 * 
 * @author hs
 */
public class IPlugRegistryTest extends TestCase {

    /**
     * INGRID-28
     */
    public void testParseMetaData() {
        fail("INGRID-28 not yet implemented");
    }

    /**
     * INGRID-29
     * 
     * @throws Exception
     */
    public void testStoreConnectionSettingsLocally() throws Exception {
        new RegistryTest().testAddAndGet();
    }

    /**
     * INGRID-30
     * 
     * @throws Exception
     */
    public void testStoreIndexStructures() throws Exception {
        new RegistryTest().testAddAndGet();
        new SyntaxInterpreterTest().testGetIplugs_Fields();

    }

    /**
     * INGRID-31
     * 
     * @throws Exception
     */
    public void testStoreIndexStructuresTemporary() throws Exception {
        new RegistryTest().testGetAllIPlugs();
    }

    /**
     * INGRID-32
     * 
     * @throws Exception
     * 
     */
    public void testStoreDataInRAM() throws Exception {
        new RegistryTest().testAddAndGet();
    }

    /**
     * INGRID-33
     */
    public void testSearchAPI() {
        fail("INGRID-33 not yet implemented");
    }

}
