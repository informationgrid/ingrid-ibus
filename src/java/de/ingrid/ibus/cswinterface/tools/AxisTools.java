/*
 * Created on 30.09.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.ibus.cswinterface.tools;

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
import org.apache.axis.MessageContext;

//import javax.xml.soap.SOAPPart;
import org.apache.axis.SOAPPart;

import org.apache.axis.soap.SOAP12Constants;
import org.apache.axis.soap.SOAP11Constants;
import org.apache.axis.soap.SOAPConstants;

//import junit.framework.TestCase;

import org.apache.axis.client.Call;
import org.custommonkey.xmlunit.XMLTestCase;

//import javax.xml.soap.Name;

import org.w3c.dom.Element;
//import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPElement;



/**
 * @author rschaefer
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class AxisTools {
    
    

    
    /**
     * 
     */
    private AxisTools() { }
    
    
    
   
    
    /**
     * @param xmlString String
     * @return smsg Message the Axis Message
     * @throws Exception e
     */
    public static Message createSOAPMessage(final String xmlString) throws Exception {
        
        System.setProperty("javax.xml.soap.MessageFactory", 
                "org.apache.axis.soap.MessageFactoryImpl");
        
        System.setProperty("javax.xml.soap.SOAPFactory", 
                "org.apache.axis.soap.SOAPFactoryImpl");
//        
//        
//        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", 
//	                     "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
        
      
//        Message axisSoapMessage = null;
//        
//        MessageFactory messageFactory = null;
//        
//       SOAPFactory soapFactory = null;
        
        Message smsg = null;
        
		try {
            
		    
//		    messageFactory = MessageFactory.newInstance();
//		     
//          soapFactory = SOAPFactory.newInstance();       
//		   
//    		 //	Create a message from the message factory.
//			 axisSoapMessage = (Message) messageFactory.createMessage();
//			
//	
//			 SOAPPart soapPart  = (SOAPPart) axisSoapMessage.getSOAPPart();
//			  
//			 SOAPEnvelope envelope =  (SOAPEnvelope) soapPart.getEnvelope();
//			 
//			
//			  
//			  //envelope.setEncodingStyle( SOAPConstants.URI_NS_SOAP_ENCODING );
//			  
//			  //envelope.setEncodingStyle( SOAPConstants.URI_NS_SOAP_ENVELOPE );
//			
//			  envelope.setSoapConstants(SOAPConstants.SOAP12_CONSTANTS);
//
//			 
//			  
//			  // remove all header information from envelope
//			  //envelope.getHeader().detachNode();
//
//			  //envelope.removeHeaders();
//			 
//			  
//			  // create a SOAP body
//			  SOAPBody body = envelope.getBody();
			 		  
//			  MessageElement cap = (MessageElement) soapFactory.createElement("GetCapabilities");
//			  
//			  MessageElement accVer = (MessageElement) soapFactory.createElement("AcceptVersions");
//			  
//			  MessageElement ver = (MessageElement) soapFactory.createElement("Version");
//			 
//			  cap.addAttribute(soapFactory.createName("service"), "CSW");
//			  
//			  ver.addTextNode("2.0");
//			  
//			  accVer.addChildElement(ver);
//			  
//			  cap.addChildElement(accVer);
//			  
//			  body.addChildElement(cap);
			
			  //axisSoapMessage.dispose();
			  		  
//			    Name ns0 =   envelope.createName("TestNS0", "ns0", "http://example.com");
//		    	Name ns1 =   envelope.createName("TestNS1", "ns1", "http://example.com");
//		    	SOAPElement bodyElmnt = body.addBodyElement(ns0);
//		    	SOAPElement el = bodyElmnt.addChildElement(ns1);
//		    	el.addTextNode("TEST RESPONSE");
			  
		        //MessageFactory mf = MessageFactory.newInstance();
		        
		        //SOAPMessage smsg =
		        //        mf.createMessage(new MimeHeaders(), new ByteArrayInputStream(xmlString.getBytes()));
		        
		        
		        smsg = new Message(xmlString);
		        
//		        MessageContext messCont = smsg.getMessageContext();
//		            
//		        messCont.setSOAPConstants(SOAPConstants.SOAP12_CONSTANTS);
		        
		        //smsg = (Message) mf.createMessage(new MimeHeaders(), null);
		        
		      
		        
		        SOAPPart sp = (SOAPPart) smsg.getSOAPPart();
		        
		        SOAPEnvelope se = (SOAPEnvelope) sp.getEnvelope();

		        //set SOAP Version 
		        se.setSoapConstants(SOAPConstants.SOAP12_CONSTANTS);
		        //se.setSoapConstants(SOAPConstants.SOAP11_CONSTANTS);
			  
		 
			  // set the saves into the structure
			 // axisSoapMessage.saveChanges();
			    	 			  
//             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//				
//             smsg.writeTo(byteArrayOutputStream);
//                     
//              System.out.println("createSOAPMessage soapMessage " + 
//                      byteArrayOutputStream.toString());
			  
			
//            Call call = new Call("http://localhost:8080/csw/csw");
//            
//            call.setSOAPVersion(SOAPConstants.SOAP12_CONSTANTS);
//            
//           //call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
//           
//            //call.setRequestMessage(axisSoapMessage);
//            
//            //call.invoke();
//            
//            call.invoke(se);
            
            //call.invoke(smsg);
            
           
            
            //Message mess = new org.apache.axis.Message("<Envelope></Envelope>");
            
            //Message mess = new org.apache.axis.Message(envelope);
            
            //call.invoke(mess);
            
            
            
        } catch (Exception e) {
           
           // e.printStackTrace();
           throw e;
        }
	
        //return axisSoapMessage;
        
        return smsg;
    }
    
    /**
     * This method returns true if the message 
     * is SOAP version 1.2.
     * @param message Message
     * @return isSOAP12 boolean
     * @throws Exception e
     */
    public static boolean isSOAP12(final Message message) throws Exception {
        
        boolean isSOAP12 = false;
        
        SOAPConstants soapconsts = null;
        
        SOAPPart sp = (SOAPPart) message.getSOAPPart();
        
        SOAPEnvelope se = (SOAPEnvelope) sp.getEnvelope();
        
        soapconsts = se.getSOAPConstants();
      
        if (soapconsts == null) {
            
            Exception e = new Exception("soapconsts is null.");
            
            throw e;
            
        }
        
        
	    if (soapconsts instanceof SOAP12Constants) {
	      
	        System.out.println("message is SOAP 1.2. ");
	        
	        isSOAP12 = true;
	        
	    } else if (soapconsts instanceof SOAP11Constants) {
	       
	        System.out.println("message is SOAP 1.1. ");
	        
	        isSOAP12 = false;
	    }
	    
        
        
        
        return isSOAP12;
    }
    
    
}
