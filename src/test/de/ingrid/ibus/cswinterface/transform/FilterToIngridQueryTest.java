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
import de.ingrid.utils.query.IngridQuery;

import junit.framework.TestCase;

/**
 * @author rschaefer
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FilterToIngridQueryTest extends TestCase {

    
    
    /**
     * @throws Exception e
     */
    public final void testGenerateQueryFromFilter() throws Exception {
        //TODO Implement generateQueryFromFilter().
        
        IngridQuery ingridQuery = null;
        
        FilterImpl filter = null;
        
        FilterToIngridQuery filterToIngridQuery = new FilterToIngridQuery();
        
//        filter = getFilterFromMessagestring(TestRequests.GETREC1);
//        
//        ingridQuery = filterToIngridQuery.generateQueryFromFilter(filter);
//        
//        System.out.println(" FilterToIngridQuery - lucene query: " + filterToIngridQuery .toString());
//        
//        System.out.println(" FilterToIngridQuery: " + ingridQuery.toLogExp());
        
        
        
        filter = getFilterFromMessagestring(TestRequests.GETREC2);
        
        ingridQuery = filterToIngridQuery.generateQueryFromFilter(filter);
        
        System.out.println(" FilterToIngridQuery - lucene query: " + filterToIngridQuery .toString());
        
        //System.out.println(" FilterToIngridQuery: " + ingridQuery.toLogExp());
        
        
        
        //assertEquals("","");
        
     }
    
    private final FilterImpl getFilterFromMessagestring(String string) throws Exception {
        
        
        
        SOAPMessage soapMessageRequest = null;
        
        SOAPElement elem = null;
        
        Element  elemFilter = null;
        
        soapMessageRequest = AxisTools.createSOAPMessage(string);
        
        elem = (SOAPElement) soapMessageRequest.getSOAPBody().getElementsByTagName("Filter").item(0);
        
        RequestTransformer requestTransformer = new RequestTransformer();
        
        return requestTransformer.getFilterFromSOAPElem(elem);
        
    }

}
