/*
 * **************************************************-
 * Ingrid iBus
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: DispatcherTest.java,v $
 */
package de.ingrid.ibus.v01;

import junit.framework.TestCase;
import de.ingrid.ibus.registry.RegistryTest;
import de.ingrid.ibus.registry.SyntaxInterpreterTest;

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
     * @throws Exception 
     */
    public void testSearchAPI() throws Exception {
        new SyntaxInterpreterTest().testGetIplugs_Fields();
        new SyntaxInterpreterTest().testGetIplugs_FieldsAndDataTypes();
        new SyntaxInterpreterTest().testGetIPlugs_DataTypes();
    }
}
