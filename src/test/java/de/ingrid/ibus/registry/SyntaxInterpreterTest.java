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
import de.ingrid.ibus.processor.UdkMetaclassPreProcessor;
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
        this.registry = new Registry(100000, true, new DummyProxyFactory());
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
        this.descriptions[1].addDataType("UDK2");
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
    public void testGetIPlugs_DataTypesAndPartner() throws Exception {
        this.descriptions[0].addDataType("UDK");
        this.descriptions[0].addPartner("bund");
        this.descriptions[1].addDataType("UDK");
        this.descriptions[1].addPartner("he");

        assertEquals(2, getIPlugs("query datatype:UDK").length);
        assertEquals(1, getIPlugs("query datatype:UDK partner:bund").length);
        assertEquals(1, getIPlugs("query datatype:UDK partner:he").length);
        assertEquals(1, getIPlugs("query datatype:UDK -partner:he").length);
        assertEquals(0, getIPlugs("query datatype:UDK -partner:he -partner:bund").length);
    }

    /**
     * 3250
     * @throws Exception
     */
//    public void testPerformance() throws Exception {
//        this.descriptions[0].addDataType("UDK");
//        this.descriptions[0].addPartner("bund");
//        this.descriptions[1].addDataType("UDK");
//        this.descriptions[1].addPartner("he");
//        long time = System.currentTimeMillis();
//        for (int i = 0; i < 10000; i++) {
//            getIPlugs("query iplugs:plug1");
//            getIPlugs("query datatype:UDK");
//            getIPlugs("query datatype:UDK partner:bund");
//            getIPlugs("query datatype:UDK partner:he");
//        }
//        System.out.println("tokk :"+(System.currentTimeMillis()-time));
//
//    }

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
        Registry aRegestry = new Registry(10, true, new DummyProxyFactory());
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
        Registry aRegestry = new Registry(10, true, new DummyProxyFactory());
        aRegestry.setCommunication(new DummyCommunication());
        PlugDescription description = new PlugDescription();
        description.setProxyServiceURL("/:23");
        description.addField("datatype");
        description.addDataType("www");
        description.setRankinTypes(true, false, false);
        aRegestry.addPlugDescription(description);
        IngridQuery query = QueryStringParser.parse("datatype:www ranking:score");

        assertEquals(1, SyntaxInterpreter.getIPlugsForQuery(query, aRegestry).length);

        aRegestry = new Registry(10, true, new DummyProxyFactory());
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

    /**
     * @throws Exception
     */
    public void testFilterRanked() throws Exception {
        // get unranked (1 ranked/ 5 not)
        PlugDescription description = new PlugDescription();
        description.setProxyServiceURL("/:23");
        description.setRankinTypes(true, false, false);
        this.registry.addPlugDescription(description);
        IngridQuery query = QueryStringParser.parse("query ranking:off");
        assertEquals(5, SyntaxInterpreter.getIPlugsForQuery(query, this.registry).length);

    }

    private PlugDescription[] getIPlugs(String queryString) throws Exception {
        QueryStringParser parser = new QueryStringParser(new StringReader(queryString));
        IngridQuery query = parser.parse();
        return SyntaxInterpreter.getIPlugsForQuery(query, this.registry);
    }

    private PlugDescription[] getIPlugs(String queryString, FieldQuery fieldQuery) throws Exception {
        QueryStringParser parser = new QueryStringParser(new StringReader(queryString));
        IngridQuery query = parser.parse();
        query.addField(fieldQuery);
        return SyntaxInterpreter.getIPlugsForQuery(query, this.registry);
    }

    private PlugDescription[] getIPlugs(String queryString, FieldQuery[] fieldQuerys) throws Exception {
        QueryStringParser parser = new QueryStringParser(new StringReader(queryString));
        IngridQuery query = parser.parse();
        for (int i = 0; i < fieldQuerys.length; i++) {
            query.addField(fieldQuerys[i]);
        }
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

    /**
     * @throws Exception
     */
    public void testGetIPlugs_IPlugs() throws Exception {
        assertEquals(0, getIPlugs("query", new FieldQuery(true, false, IngridQuery.IPLUGS, "abc")).length);
        assertEquals(1, getIPlugs("query", new FieldQuery(true, false, IngridQuery.IPLUGS, this.descriptions[0]
                .getPlugId())).length);
        assertEquals(1, getIPlugs("query", new FieldQuery(true, false, IngridQuery.IPLUGS, this.descriptions[1]
                .getPlugId())).length);

        assertEquals(2, getIPlugs("query", new FieldQuery[] {
                new FieldQuery(true, false, IngridQuery.IPLUGS, this.descriptions[0].getPlugId()),
                new FieldQuery(true, false, IngridQuery.IPLUGS, this.descriptions[1].getPlugId()) }).length);

        // using query parser
        IngridQuery query = QueryStringParser.parse("aQuery " + IngridQuery.IPLUGS + ":abc");
        assertEquals(0, SyntaxInterpreter.getIPlugsForQuery(query, this.registry).length);
        // FIXME the below does not work, cause "plugId" is parsed to "plugId"
        // and not ot plugIdF
        // query = QueryStringParser.parse("aQuery " + IngridQuery.IPLUGS +
        // ":\"" + this.descriptions[0].getPlugId()+"\"");
        // assertEquals(1, SyntaxInterpreter.getIPlugsForQuery(query,
        // this.registry).length);
    }

    /**
     * @throws Exception
     */
    public void testMetaclass() throws Exception {
        for (int i = 0; i < this.descriptions.length; i++) {
            this.descriptions[i].addField(UdkMetaclassPreProcessor.PORTAL_METACLASS);
        }
        assertEquals(this.descriptions.length, getIPlugs("query", new FieldQuery(true, false,
                UdkMetaclassPreProcessor.PORTAL_METACLASS, UdkMetaclassPreProcessor.UDK_METACLASS_DATABASE)).length);
    }

}
