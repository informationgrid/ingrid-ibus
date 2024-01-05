/*
 * **************************************************-
 * InGrid iBus
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
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
package de.ingrid.ibus.comm.v01;

import de.ingrid.ibus.comm.registry.RegistryTest;
import de.ingrid.ibus.comm.registry.SyntaxInterpreterTest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test for iplug-registry-feature (INGRID-5). created on 21.07.2005
 * <p>
 * 
 * @author hs
 */
public class IPlugRegistryTest {

    /**
     * INGRID-28
     */
    @Test
    public void testParseMetaData() {
        fail("INGRID-28 not yet implemented");
    }

    /**
     * INGRID-29
     * 
     * @throws Exception
     */
    @Test
    public void testStoreConnectionSettingsLocally() throws Exception {
        new RegistryTest().testAddAndGet();
    }

    /**
     * INGRID-30
     * 
     * @throws Exception
     */
    @Test
    public void testStoreIndexStructures() throws Exception {
        new RegistryTest().testAddAndGet();
        new de.ingrid.ibus.comm.registry.SyntaxInterpreterTest().testGetIplugs_Fields();

    }

    /**
     * INGRID-31
     * 
     * @throws Exception
     */
    @Test
    public void testStoreIndexStructuresTemporary() throws Exception {
        new RegistryTest().testGetAllIPlugs();
    }

    /**
     * INGRID-32
     * 
     * @throws Exception
     * 
     */
    @Test
    public void testStoreDataInRAM() throws Exception {
        new RegistryTest().testAddAndGet();
    }

    /**
     * INGRID-33
     * @throws Exception 
     */
    @Test
    public void testSearchAPI() throws Exception {
        new de.ingrid.ibus.comm.registry.SyntaxInterpreterTest().testGetIplugs_Fields();
        new de.ingrid.ibus.comm.registry.SyntaxInterpreterTest().testGetIplugs_FieldsAndDataTypes();
        new SyntaxInterpreterTest().testGetIPlugs_DataTypes();
    }
}
