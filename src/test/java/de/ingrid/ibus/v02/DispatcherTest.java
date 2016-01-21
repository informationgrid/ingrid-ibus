/*
 * **************************************************-
 * InGrid iBus
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
        new BusTest().testSearchWithStatisticProcessors();
    }
}
