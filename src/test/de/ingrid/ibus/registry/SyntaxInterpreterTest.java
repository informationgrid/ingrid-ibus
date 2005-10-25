/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.registry;

import java.io.StringReader;

import junit.framework.TestCase;
import de.ingrid.iplug.PlugDescription;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * SyntaxInterpreterTest
 * 
 * <p/>created on 07.09.2005
 * 
 * @version $Revision: $
 * @author sg
 * @author $Author: ${lastedit}
 * 
 */
public class SyntaxInterpreterTest extends TestCase {

    private Registry registry;

    private PlugDescription[] descriptions = new PlugDescription[5];

    /**
     * Call setUp for feature tests
     * 
     * @throws Exception
     */
    public SyntaxInterpreterTest() throws Exception {
        setUp();
    }

    protected void setUp() throws Exception {
        this.registry = new Registry(100000);
        for (int i = 0; i < this.descriptions.length; i++) {
            this.descriptions[i] = new PlugDescription();
            this.descriptions[i].setPlugId("plug" + i);
            this.registry.addIPlug(this.descriptions[i]);
        }
    }

    /**
     * @throws Exception
     */
    public void testGetIPlugs_NoTermsNoFileds() throws Exception {
        assertEquals(0, getIPlugs("").length);
        this.descriptions[0].setDataType("UDK");
        assertEquals(0, getIPlugs("datatype:UDK").length);
    }

    /**
     * @throws Exception
     */
    public void testGetIPlugs_DataTypes() throws Exception {
        this.descriptions[0].setDataType("UDK");
        this.descriptions[1].setDataType("UDK");
        assertEquals(2, getIPlugs("datatype:UDK aQuery").length);
    }

    /**
     * @throws Exception
     */
    public void testGetIPlugs_NoFieldsNoDataDypes() throws Exception {
        this.descriptions[0].setDataType("UDK");
        this.descriptions[0].addField("field1");
        this.descriptions[1].addField("field1");
        assertEquals(this.descriptions.length, getIPlugs("aQuery").length);

    }

    /**
     * @throws Exception
     */
    public void testGetIplugs_FieldsAndDataTypes() throws Exception {
        this.descriptions[0].setDataType("UDK");
        this.descriptions[0].addField("field1");
        this.descriptions[1].setDataType("UDK");
        this.descriptions[1].addField("field2");

        this.descriptions[2].setDataType("CSW");
        this.descriptions[2].addField("field1");
        this.descriptions[3].setDataType("CSW");
        this.descriptions[3].addField("field2");

        assertEquals(1, getIPlugs("datatype:UDK field1:aField").length);
        assertEquals(1, getIPlugs("datatype:UDK field2:aField").length);
        assertEquals(1, getIPlugs("datatype:CSW field1:aField").length);
        assertEquals(1, getIPlugs("datatype:CSW field2:aField").length);

        this.descriptions[4].setDataType("UDK");
        this.descriptions[4].addField("field1");
        assertEquals(2, getIPlugs("datatype:UDK field1:aField").length);
    }

    /**
     * 
     * @throws Exception
     */
    public void testGetIplugs_Fields() throws Exception {
        for (int i = 0; i < this.descriptions.length; i++) {
            this.descriptions[i].addField("field" + i);
        }
        for (int i = 0; i < this.descriptions.length; i++) {
            assertEquals(1, getIPlugs("field" + i + ":aField").length);
        }

        QueryStringParser parser = new QueryStringParser(new StringReader("a simple Query"));
        IngridQuery query = parser.parse();
        assertEquals(this.descriptions.length, SyntaxInterpreter.getIPlugsForQuery(query, this.registry).length);
    }

    private PlugDescription[] getIPlugs(String queryString) throws Exception {
        QueryStringParser parser = new QueryStringParser(new StringReader(queryString));
        IngridQuery query = parser.parse();
        return SyntaxInterpreter.getIPlugsForQuery(query, this.registry);
    }

}
