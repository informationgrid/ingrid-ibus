/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.registry;

import java.io.StringReader;

import junit.framework.TestCase;
import de.ingrid.utils.PlugDescription;
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
    public void testGetIPlugs_NoTermsNoFields() throws Exception {
        assertEquals(0, getIPlugs("").length);
        // this.descriptions[0].addDataType("UDK"); // as soon a datatype is
        // setted we get all plugs that suppor this datatype now.
        assertEquals(0, getIPlugs("datatype:UDK").length);
    }

    /**
     * @throws Exception
     */
    public void testGetIPlugs_DataTypes() throws Exception {
        this.descriptions[0].addDataType("UDK");
        this.descriptions[1].addDataType("UDK");
        assertEquals(2, getIPlugs("datatype:UDK aQuery").length);
    }

    /**
     * @throws Exception
     */
    public void testGetIPlugs_NoFieldsNoDataDypes() throws Exception {
        this.descriptions[0].addDataType("UDK");
        this.descriptions[0].addField("field1");
        this.descriptions[1].addField("field1");
        assertEquals(this.descriptions.length, getIPlugs("aQuery").length);

    }

    /**
     * @throws Exception
     */
    public void testGetIplugs_FieldsAndDataTypes() throws Exception {
        this.descriptions[0].addDataType("UDK");
        this.descriptions[0].addField("field1");
        this.descriptions[1].addDataType("UDK");
        this.descriptions[1].addField("field2");

        this.descriptions[2].addDataType("CSW");
        this.descriptions[2].addField("field1");
        this.descriptions[3].addDataType("CSW");
        this.descriptions[3].addField("field2");

        assertEquals(1, getIPlugs("datatype:UDK field1:aField").length);
        assertEquals(1, getIPlugs("datatype:UDK field2:aField").length);
        assertEquals(1, getIPlugs("datatype:CSW field1:aField").length);
        assertEquals(1, getIPlugs("datatype:CSW field2:aField").length);

        this.descriptions[4].addDataType("UDK");
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

        QueryStringParser parser = new QueryStringParser(new StringReader(
                "a simple Query"));
        IngridQuery query = parser.parse();
        assertEquals(this.descriptions.length, SyntaxInterpreter
                .getIPlugsForQuery(query, this.registry).length);
    }

    public void testDataTypeQueries() throws Exception {
        Registry aRegestry = new Registry(10);
        PlugDescription description = new PlugDescription();
        description.setPlugId("23");
        description.addField("datatype");
        description.addDataType("www");
        aRegestry.addIPlug(description);
        IngridQuery query = QueryStringParser.parse("datatype:www");

        assertEquals(1,
                SyntaxInterpreter.getIPlugsForQuery(query, aRegestry).length);

    }
    
    public void testIsRanked() throws Exception {
    	Registry aRegestry = new Registry(10);
        PlugDescription description = new PlugDescription();
        description.setPlugId("23");
        description.addField("datatype");
        description.addDataType("www");
        description.setRankinTypes(true, false, false);
        aRegestry.addIPlug(description);
             IngridQuery query = QueryStringParser.parse("datatype:www ranking:score");

        assertEquals(1,
                SyntaxInterpreter.getIPlugsForQuery(query, aRegestry).length);
      
        aRegestry = new Registry(10);
         description = new PlugDescription();
        description.setPlugId("23");
        description.addField("datatype");
        description.addDataType("www");
        description.setRankinTypes(false, false, true);
        aRegestry.addIPlug(description);
              query = QueryStringParser.parse("datatype:www ranking:sore");

        assertEquals(0,
                SyntaxInterpreter.getIPlugsForQuery(query, aRegestry).length);
	}

    private PlugDescription[] getIPlugs(String queryString) throws Exception {
        QueryStringParser parser = new QueryStringParser(new StringReader(
                queryString));
        IngridQuery query = parser.parse();
        return SyntaxInterpreter.getIPlugsForQuery(query, this.registry);
    }

}
