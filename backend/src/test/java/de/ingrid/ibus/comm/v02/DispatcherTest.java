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

package de.ingrid.ibus.comm.v02;

import de.ingrid.ibus.comm.BusTest;

import org.junit.jupiter.api.Test;
import de.ingrid.ibus.comm.registry.SyntaxInterpreterTest;

/**
 * Test for dispatcher feature (INGRID-2)
 * 
 * <p/> created on 21.07.2005
 * 
 * 
 * @author hs
 */
public class DispatcherTest {

    /**
     * INGRID-18
     * @throws Exception 
     */
    @Test
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
    @Test
    public void testReturnOfResults() throws Exception {
        new BusTest().testSearch();
    }

    /**
     * INGRID-20
     * @throws Exception 
     * @throws Exception 
     */
    @Test
    public void testCommunicationWithStatisticOfAccesses() throws Exception {
        new BusTest().testSearchWithStatisticProcessors();
    }
}
