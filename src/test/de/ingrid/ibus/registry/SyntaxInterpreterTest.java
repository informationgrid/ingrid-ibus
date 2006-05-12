/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus.registry;

import java.io.StringReader;

import junit.framework.TestCase;
import de.ingrid.ibus.DummyCommunication;
import de.ingrid.ibus.DummyProxyFactory;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.query.FieldQuery;
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
        this.registry = new Registry(100000, true,new DummyProxyFactory());
        this.registry.setCommunication(new DummyCommunication());
        for (int i = 0; i < this.descriptions.length; i++) {
            this.descriptions[i] = new PlugDescription();
            this.descriptions[i].setProxyServiceURL("/bus:plug" + i);
            this.registry.addPlugDescription(this.descriptions[i]);
        }
    }

    /**
     * @throws Exception
     */
    public void testGetIPlugs_NoTermsNoFields() throws Exception {
        assertEquals(5, getIPlugs("").length); // no limitations at all.
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
        // using query parser
        assertEquals(0, getIPlugs("-datatype:UDK aQuery").length);
        IngridQuery query = QueryStringParser.parse("aQuery");
        query.addField(new FieldQuery(false, true, "datatype", "UDK"));
        PlugDescription[] plugsForQuery = SyntaxInterpreter.getIPlugsForQuery(query, this.registry);
        assertEquals(0, plugsForQuery.length);
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
        this.descriptions[1].addField("FIeld2");

        this.descriptions[2].addDataType("CSW");
        this.descriptions[2].addField("field1");
        this.descriptions[3].addDataType("CSW");
        this.descriptions[3].addField("field2");

        assertEquals(1, getIPlugs("datatype:UDK Field1:aField").length);
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

        QueryStringParser parser = new QueryStringParser(new StringReader("a simple Query"));
        IngridQuery query = parser.parse();
        assertEquals(this.descriptions.length, SyntaxInterpreter.getIPlugsForQuery(query, this.registry).length);

    }

    /**
     * @throws Exception
     */
    public void testDataTypeQueries() throws Exception {
        Registry aRegestry = new Registry(10, true,new DummyProxyFactory());
        aRegestry.setCommunication(new DummyCommunication());
        PlugDescription description = new PlugDescription();
        description.setProxyServiceURL("/:23");
        description.addField("datatype");
        description.addDataType("www");
        aRegestry.addPlugDescription(description);
        IngridQuery query = QueryStringParser.parse("datatype:www");

        assertEquals(1, SyntaxInterpreter.getIPlugsForQuery(query, aRegestry).length);

    }

    /**
     * @throws Exception
     */
    public void testIsRanked() throws Exception {
        Registry aRegestry = new Registry(10, true,new DummyProxyFactory());
        aRegestry.setCommunication(new DummyCommunication());
        PlugDescription description = new PlugDescription();
        description.setProxyServiceURL("/:23");
        description.addField("datatype");
        description.addDataType("www");
        description.setRankinTypes(true, false, false);
        aRegestry.addPlugDescription(description);
        IngridQuery query = QueryStringParser.parse("datatype:www ranking:score");

        assertEquals(1, SyntaxInterpreter.getIPlugsForQuery(query, aRegestry).length);

        aRegestry = new Registry(10, true,new DummyProxyFactory());
        aRegestry.setCommunication(new DummyCommunication());
        description = new PlugDescription();
        description.setProxyServiceURL("/:23");
        description.addField("datatype");
        description.addDataType("www");
        description.setRankinTypes(false, false, true);
        aRegestry.addPlugDescription(description);
        query = QueryStringParser.parse("datatype:www ranking:sore");

        assertEquals(0, SyntaxInterpreter.getIPlugsForQuery(query, aRegestry).length);
    }

    private PlugDescription[] getIPlugs(String queryString) throws Exception {
        QueryStringParser parser = new QueryStringParser(new StringReader(queryString));
        IngridQuery query = parser.parse();
        return SyntaxInterpreter.getIPlugsForQuery(query, this.registry);
    }

    /**
     * @throws Exception
     */
    public void testGetIPlugs_Provider() throws Exception {
        this.descriptions[0].addProvider("anhalt");
        this.descriptions[1].addProvider("berlin");
        assertEquals(1, getIPlugs("provider:anhalt aQuery").length);
        assertEquals(1, getIPlugs("provider:berlin aQuery").length);
        assertEquals(0, getIPlugs("provider:hessen aQuery").length);
        // using query parser
        assertEquals(4, getIPlugs("-provider:berlin aQuery").length);

        IngridQuery query = QueryStringParser.parse("aQuery");
        query.addField(new FieldQuery(true, false, "provider", "anhalt"));
        PlugDescription[] plugsForQuery = SyntaxInterpreter.getIPlugsForQuery(query, this.registry);
        assertEquals(1, plugsForQuery.length);
    }
    
//    public void testGetI() throws Exception {
//        IngridQuery query = QueryStringParser.parse("aQuery iplugs:\"a\" iplugs:b");
//        System.out.println(Arrays.asList(query.getIPlugs()));
//        query = QueryStringParser.parse("aQuery provider:\"a\" provider:b");
//        System.out.println(Arrays.asList(query.getPositiveProvider()));
//    }

    /**
     * @throws Exception
     */
    public void testGetIPlugs_IPlugs() throws Exception {
        assertEquals(0, getIPlugs("query " + IngridQuery.IPLUGS + ":" + 123).length);
        assertEquals(1, getIPlugs("query " + IngridQuery.IPLUGS + ":" + this.descriptions[0].getPlugId().hashCode()).length);
        assertEquals(1, getIPlugs("query " + IngridQuery.IPLUGS + ":" + this.descriptions[1].getPlugId().hashCode()).length);

        assertEquals(2, getIPlugs("query " + IngridQuery.IPLUGS + ":" + this.descriptions[0].getPlugId().hashCode()+" "
                + IngridQuery.IPLUGS + ":" + this.descriptions[1].getPlugId().hashCode()).length);
        // using query parser
        IngridQuery query = QueryStringParser.parse("aQuery");
        query.addField(new FieldQuery(true, false, IngridQuery.IPLUGS, this.descriptions[0].getPlugId().hashCode()+""));
        PlugDescription[] plugsForQuery = SyntaxInterpreter.getIPlugsForQuery(query, this.registry);
        assertEquals(1, plugsForQuery.length);
    }

}
