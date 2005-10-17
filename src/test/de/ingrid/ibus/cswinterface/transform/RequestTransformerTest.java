/*
 * Created on 07.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.ibus.cswinterface.transform;

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

    public final void testTransform() throws Exception {
        //TODO Implement transform().
       
        RequestTransformer requestTransformer = new RequestTransformer();
        
        IngridQuery ingridQuery = null;
        
        SOAPElement soapElementFilter = getSOAPElementFilterFromString(TestRequests.GETREC1);
        
        ingridQuery = requestTransformer.transform(soapElementFilter);
        
        assertNotNull(ingridQuery);
        
       //TODO compare query strings ..
        //System.out.println("RequestTransformerTest IngridQuery: " + ingridQuery.toLogExp());
        
    }
    
    
    private final SOAPElement getSOAPElementFilterFromString(String string) throws Exception {
        
        SOAPElement soapElementFilter = null;
        
        SOAPMessage soapMessageRequest = null;
        
        soapMessageRequest = AxisTools.createSOAPMessage(string);
        
        soapElementFilter = (SOAPElement) soapMessageRequest.getSOAPBody().getElementsByTagName("Filter").item(0);
        
        return soapElementFilter;
    }

}
