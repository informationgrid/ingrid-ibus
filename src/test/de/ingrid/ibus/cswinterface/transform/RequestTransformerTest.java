/*
 * Created on 07.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.ibus.cswinterface.transform;

import java.util.ArrayList;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import de.ingrid.ibus.cswinterface.TestRequests;
import de.ingrid.ibus.cswinterface.tools.AxisTools;
import de.ingrid.utils.query.IngridQuery;

import junit.framework.TestCase;

/**
 * @author rschaefer
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RequestTransformerTest extends TestCase {

    public final void testTransformFilter() throws Exception {
        //TODO Implement transform().
       
        RequestTransformer requestTransformer = new RequestTransformer();
        
        IngridQuery ingridQuery = null;
        
        SOAPElement soapElementFilter = null;
        
        IngridQueryToString ingridQueryToString = new IngridQueryToString();
        
        
        soapElementFilter = getSOAPElementFilterFromString(TestRequests.GETREC1);
        
        String queryString = null;
        
        ingridQuery = requestTransformer.transform(soapElementFilter);
        
        assertNotNull(ingridQuery);
        
       
        
        
        queryString = ingridQueryToString.transform(ingridQuery);
        
       
        
        assertEquals("(AND ( AND t0:2005-10-20  OR title:Test ))", queryString);
        
        
        
        soapElementFilter = getSOAPElementFilterFromString(TestRequests.GETREC3);
        
       
        
        ingridQuery = requestTransformer.transform(soapElementFilter);
        
        assertNotNull(ingridQuery);
        
       
    
        
        queryString = ingridQueryToString.transform(ingridQuery);
        
        
        assertEquals("(AND ( AND anyText:fische  AND area:halle NOT ( AND anyText:saale  OR anyText:hufeisensee )))", queryString);
        
        
        //System.out.println("get records as ingrid query string: " + queryString);
        
    }
    
    
    public final void testTransformList() throws Exception {
        //TODO Implement transform().
       
        RequestTransformer requestTransformer = new RequestTransformer();
        
        IngridQuery ingridQuery = null;
        
        ArrayList idsList = new ArrayList();
        
        String queryString = null;
        
        idsList.add("AID");
        
        idsList.add("BID");
        
        idsList.add("CID");
   
        ingridQuery = requestTransformer.transform(idsList);
        
        assertNotNull(ingridQuery);
        
       
        IngridQueryToString ingridQueryToString = new IngridQueryToString();
        
        queryString = ingridQueryToString.transform(ingridQuery);
        
        //System.out.println("get record by id as lucene query: " + queryString);
        
        assertEquals("( AND ID:AID  AND ID:BID  AND ID:CID )", queryString);
        
        
    }
    
    
    
    private final SOAPElement getSOAPElementFilterFromString(String string) throws Exception {
        
        SOAPElement soapElementFilter = null;
        
        SOAPMessage soapMessageRequest = null;
        
        soapMessageRequest = AxisTools.createSOAPMessage(string);
        
        soapElementFilter = (SOAPElement) soapMessageRequest.getSOAPBody().getElementsByTagName("Filter").item(0);
        
        return soapElementFilter;
    }

}
