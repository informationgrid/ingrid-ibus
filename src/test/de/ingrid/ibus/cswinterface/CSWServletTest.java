/*
 * Created on 28.09.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.ibus.cswinterface;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
//import java.io.IOException;

import javax.xml.soap.MessageFactory;
//import org.apache.axis.soap.MessageFactoryImpl;

import javax.xml.soap.SOAPBody;
//import javax.xml.soap.SOAPConstants;
//import javax.xml.soap.SOAPElement;
import org.apache.axis.message.MessageElement;
//import javax.xml.soap.SOAPEnvelope;
import org.apache.axis.message.SOAPEnvelope;
//import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
//import org.apache.axis.soap.SOAPFactoryImpl;
import javax.xml.soap.SOAPMessage;
 import org.apache.axis.Message;

//import javax.xml.soap.SOAPPart;
import org.apache.axis.SOAPPart;

import org.apache.axis.soap.SOAPConstants;

//import junit.framework.TestCase;

import org.apache.axis.client.Call;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.ElementNameAndTextQualifier;
import org.custommonkey.xmlunit.XMLTestCase;

//import javax.xml.soap.Name;

import org.w3c.dom.Element;

import de.ingrid.ibus.cswinterface.CSWServlet;
import de.ingrid.ibus.cswinterface.exceptions.CSWOperationNotSupportedException;
import de.ingrid.ibus.cswinterface.tools.AxisTools;
//import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPElement;


/**
 * @author rschaefer
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CSWServletTest extends XMLTestCase {

    
    private CSWServlet cswServlet = null;
    
    /*
     * Class under test for void init(ServletConfig)
     */
//    public final void testInitServletConfig() {
//        //TODO Implement init().
//        
//      
//        cswServlet = new CSWServlet();
//        
//        try {
//            
//            CSWServlet httpServlet = new CSWServlet();
//            
//            httpServlet.init(cswServlet);
//            
//        } catch (ServletException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        
//    }

    
    
    public final void testOnMessage() throws Exception {
       
        
        Message soapMessageRequest = null;
        
        Message soapMessageResponse = null;
         
        
        
        assertNotNull(cswServlet);
            
        soapMessageRequest = AxisTools.createSOAPMessage(TestRequests.GETCAP1);
        
        assertNotNull(soapMessageRequest);
        
        //call onMessage without tomcat running
        soapMessageResponse = (Message) cswServlet.onMessage(soapMessageRequest);
        
      // call onMessage with tomcat running
//        Call call = new Call("http://localhost:8080/csw/csw");
//      
//        call.setSOAPVersion(SOAPConstants.SOAP12_CONSTANTS);
//      
//      //call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
//     
//        call.setRequestMessage((Message) soapMessageRequest);
//      
//        call.invoke();
//      
//      //call.invoke((Message) soapMessageRequest);
//      
//        soapMessageResponse = call.getResponseMessage();
        
        
        
        
        
        assertNotNull(soapMessageResponse);
        
        
        ByteArrayOutputStream byteArrayOutputStreamResponseTest = new ByteArrayOutputStream();
        
        //TODO create test response
        AxisTools.createSOAPMessage(TestRequests.GETCAP1).writeTo(byteArrayOutputStreamResponseTest);
        
        
        
        ByteArrayOutputStream byteArrayOutputStreamResponse = new ByteArrayOutputStream();
		
        soapMessageResponse.writeTo(byteArrayOutputStreamResponse);
        
       
       
        assertXMLEqual("comparing soapMessageResponseTest to soapMessageResponse", byteArrayOutputStreamResponseTest.toString(), 
                                                                              byteArrayOutputStreamResponse.toString());

          
        
        soapMessageRequest = AxisTools.createSOAPMessage(TestRequests.GETCAPINVALID1);
        
        
        soapMessageResponse = (Message) cswServlet.onMessage(soapMessageRequest);
        
       
      
        //System.out.println("Test: " + AxisTools.createSOAPMessage(TestResponses.EXC1).getSOAPPartAsString());
        
        //System.out.println("Original: " + soapMessageResponse.getSOAPPartAsString());
        
        
//        assertXMLEqual("comparing soapMessageResponseTest to soapMessageResponse", 
//                 AxisTools.createSOAPMessage(TestResponses.EXC1).getSOAPPartAsString(),
//                 soapMessageResponse.getSOAPPartAsString());
        
        
        
       
        
//        Diff myDiff = new Diff(TestResponses.EXC1, TestResponses.EXC2);
//
//        System.out.println("myDiff: " + myDiff);
//        
//        
//        System.out.println("myDiff.similar(): " + myDiff.similar());
//        
//        System.out.println("myDiff.identical(): " + myDiff.identical());
        
       //assertXMLEqual(TestResponses.EXC1, TestResponses.EXC2);
        
        
        String myControlXML = "<suite><test status=\"pass\">FirstTestCase</test><test status=\"pass\">SecondTestCase</test></suite>";
        //String myTestXML = "<suite><test status=\"pass\">SecondTestCase</test><test status=\"pass\">FirstTestCase</test></suite>";
        String myTestXML = "<suite><test status=\"pass\">FirstTestCase</test><test status=\"pass\">SecondTestCase</test></suite>";

        //(assertXMLNotEqual("Repeated child elements in different sequence order are not equal by default",
        //    myControlXML, myTestXML);

        Diff myDiff = new Diff(myControlXML, myTestXML);
        //myDiff.overrideElementQualifier(new ElementNameAndTextQualifier());
        assertXMLEqual("But they are equal when an ElementQualifier controls which test element is compared with each control element",
            myDiff, true);

        
        
    }

   

    /*
     * Class under test for void doGet(HttpServletRequest, HttpServletResponse)
     */
    public final void testDoGetHttpServletRequestHttpServletResponse() {
        //TODO Implement doGet().
        
        assertNotNull(cswServlet);
    }
    
    
//    private ServletConfig createServletConfig(){
//        
//       
//        
//        
//    }
    
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        cswServlet = new CSWServlet();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        
        cswServlet = null;
        
    }  
    

   
}
