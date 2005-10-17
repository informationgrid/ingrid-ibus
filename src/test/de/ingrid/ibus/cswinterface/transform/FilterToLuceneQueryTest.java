/*
 * Created on 04.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.ibus.cswinterface.transform;



import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Document;
import org.w3c.dom.Element;



import de.ingrid.ibus.cswinterface.TestRequests;
import de.ingrid.ibus.cswinterface.tools.AxisTools;
import de.ingrid.ibus.cswinterface.tools.SOAPTools;
import de.ingrid.ibus.cswinterface.tools.XMLTools;

import junit.framework.TestCase;

/**
 * @author rschaefer
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FilterToLuceneQueryTest extends TestCase {

    
    
    /**
     * @throws Exception e
     */
    public final void testGenerateQueryFromFilter() throws Exception {
        //TODO Implement generateQueryFromFilter().
        
       
        SOAPMessage soapMessageRequest = null;
        
        SOAPElement elem = null;
        
        Element  elemFilter = null;
        
        soapMessageRequest = AxisTools.createSOAPMessage(TestRequests.GETREC2);
        
        elem = (SOAPElement) soapMessageRequest.getSOAPBody().getElementsByTagName("Filter").item(0);
        
        
        Document doc = XMLTools.create();
	    
        doc.appendChild(doc.createElement("Filter"));
	    
        elemFilter = doc.getDocumentElement();
		                     elemFilter = (Element) SOAPTools.copyNode(elem, elemFilter);
        
        FilterImpl filter = new FilterImpl(elemFilter);
        
        
        FilterToLuceneQuery filterToLuceneQuery = new FilterToLuceneQuery();
        
        filterToLuceneQuery.generateQueryFromFilter(filter);
        
        System.out.println(" FilterToLuceneQuery: " + filterToLuceneQuery.toString());
        
        //assertEquals("","");
        
     }

}
