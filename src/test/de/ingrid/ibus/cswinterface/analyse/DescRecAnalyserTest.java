/*
 * Created on 04.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.ibus.cswinterface.analyse;

import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPMessage;

import de.ingrid.ibus.cswinterface.TestRequests;
import de.ingrid.ibus.cswinterface.tools.AxisTools;
import junit.framework.TestCase;

/**
 * @author rschaefer
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DescRecAnalyserTest extends TestCase {

    /*
     * Class under test for boolean analyse(SOAPBodyElement)
     */
    public final void testAnalyseSOAPBodyElement() throws Exception {


        boolean descRecRequestValid = false;
        
        SOAPMessage soapMessageRequest = null;
        
      
        DescRecAnalyser descRecAnalyser = new DescRecAnalyser();
        
        //TEST1
        soapMessageRequest = AxisTools.createSOAPMessage(TestRequests.DESCREC1);
        
        descRecRequestValid = descRecAnalyser.analyse(((SOAPBodyElement) soapMessageRequest.getSOAPBody().getFirstChild()));
       
        assertTrue(descRecRequestValid);
        
        
//      TEST2
        soapMessageRequest = AxisTools.createSOAPMessage(TestRequests.DESCREC2);
        
        descRecRequestValid = descRecAnalyser.analyse(((SOAPBodyElement) soapMessageRequest.getSOAPBody().getFirstChild()));
       
        assertTrue(descRecRequestValid);
        
        
    }

    /*
     * Class under test for boolean analyse(String)
     */
    public final void testAnalyseString() {
        //TODO Implement analyse().
    }

}
