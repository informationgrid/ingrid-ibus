/*----------------------------------------------------------------------------*
 *          @@@@@      @@@       @@@@@                                        *
 *      @@@@@@@@@@@    @@@@    @@@@@@@@        @                              *
 *     @@@@@@@@@@@@    @@@@   @@@@@@@@@     @@@@                              *
 *    @@@@@            @@@@  @@@@           @@@@                              *
 *   @@@@@             @@@@  @@@@@        @@@@@@@@@   @@@@@@@@      @@@@@@@   *
 *   @@@@    @@@@@@@   @@@@   @@@@@@@     @@@@@@@@@  @@@@@@@@@@   @@@@@@@@@   *
 *   @@@@   @@@@@@@@   @@@@    @@@@@@@@     @@@@    @@@@    @@@   @@@@        *
 *   @@@@    @@@@@@@   @@@@      @@@@@@@    @@@@    @@@@@@@@@@@@ @@@@         *
 *   @@@@@      @@@@   @@@@         @@@@    @@@@    @@@@@@@@@@@@ @@@@         *
 *    @@@@@     @@@@   @@@@   @     @@@@    @@@@    @@@@      @   @@@@        *
 *     @@@@@@@@@@@@@   @@@@   @@@@@@@@@@    @@@@@@@  @@@@@@@@@@   @@@@@@@@@   *
 *       @@@@@@@@@@@   @@@@   @@@@@@@@       @@@@@@   @@@@@@@@@     @@@@@@@   *
 *                            Neue Wege mit GIS                               *
 *                                                                            *
 * Fraunhoferstr. 5                                                           *
 * D-64283 Darmstadt                                                          *
 * info@gistec-online.de                          http://www.gistec-online.de *
 *----------------------------------------------------------------------------*
 *                                                                            *
 * Copyright © 2004 GIStec GmbH                                               *
 * ALL Rights Reserved.                                                       *
 *                                                                            *
 *+---------------------------------------------------------------------------*
 *                                                                            *
 * Author           : Ralf Schäfer                                            *
 * Erstellungsdatum : 24.05.2004                                              *
 * Version          : 1.0                                                     *
 * Beschreibung     :  Hilfsfunktionen fuer SOAP                              *
 *                                                                            *
 *                                                                            *
 *----------------------------------------------------------------------------*
 * Änderungen (Datum, Version, Author, Beschreibung)                          *
 *----------------------------------------------------------------------------*
 *            |         |          |                                          *
 *            |         |          |                                          *
 *----------------------------------------------------------------------------*
*/


package de.ingrid.ibus.cswinterface.tools;


//IMPORTS java.io
import java.io.UnsupportedEncodingException;

import java.util.Iterator;

//IMPORTS java.net
import java.net.URLDecoder;
import java.net.URLEncoder;


//IMPORTS javax.xml.soap
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.SOAPElement;

import javax.xml.soap.SOAPEnvelope;
//import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.soap.SOAPConstants;

import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPException;

import org.w3c.dom.DOMException;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;

//Import log4j classes.
import org.apache.log4j.Logger;

import de.ingrid.ibus.cswinterface.InitParameters;
//import de.ingrid.ibus.cswinterface.TestRequests;

/**
 * Diese Klasse stellt Hilfsfunktionen fuer SOAP zur Verfuegung  
 * @author rschaefer
 */
public class SOAPTools {
	
	
	private static final Logger LOGGER = Logger.getLogger(SOAPTools.class);
	
	
	
	public static final String SOAP12ENV = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        
    
        "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
        "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
        "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                 
        " <soapenv:Body>\n" +
          
          " </soapenv:Body>\n" +
        "</soapenv:Envelope>";
	


	public static final String SOAP11ENV = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        
    
//      ns just for SOAP 1.1
        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
        
        "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
        "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                 
        " <soapenv:Body>\n" +
          
          " </soapenv:Body>\n" +
        "</soapenv:Envelope>";
	
	
	
	 /**
	  * fuehrt ein Deep copy von einem Dom-Nodes zu einem Soap-Node aus
	  * @param org.w3c.dom.Node source source node
	  * @param javax.xml.soap.Node dest destination node
	  * @param SOAPEnvelope env soap envelope
	  * @return javax.xml.soap.Node dest destination
	  * @throws SOAPException 
	 */
	
	 public  static javax.xml.soap.Node copyNode(org.w3c.dom.Node source,
			   javax.xml.soap.Node dest,
			   SOAPEnvelope env) throws SOAPException {
           
				   if (source.getNodeType() == org.w3c.dom.Node.TEXT_NODE) {
					   // TODO  not really correct
					   try {
						dest.getParentElement().addTextNode(  URLEncoder.encode(source.getNodeValue(), "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						System.err.println("SOAPTools UnsupportedEncodingException: " + e);
					} catch (DOMException e) {
						System.err.println("SOAPTools DOMException: " + e);
					} catch (SOAPException e) {
						System.err.println("SOAPTools SOAPException: " + e);
					}
					   return dest;
				   } else {
                
					   org.w3c.dom.NamedNodeMap attr = source.getAttributes();
                        //TODO attr
					   if (attr != null) {
						   for (int i = 0; i < attr.getLength(); i++) {
							   Name name = env.createName( attr.item(i).getNodeName() );
							   ((SOAPElement)dest).addAttribute( name,(String)attr.item(i).getNodeValue());
						   }
					   }
                
					   org.w3c.dom.NodeList list = source.getChildNodes();
                
					   for (int i = 0; i < list.getLength(); i++) {
                    
						   if ( !(list.item(i) instanceof org.w3c.dom.Text) ) {
							   SOAPElement elem = ((SOAPElement)dest).addChildElement( list.item(i).getNodeName() );
							   Node n = copyNode(list.item(i), elem, env);
						   } else {
						   	
							   try {
								((SOAPElement)dest).addTextNode( URLEncoder.encode(list.item(i).getNodeValue(), "UTF-8"));
							} catch (UnsupportedEncodingException e) {
								System.err.println("SOAPTools UnsupportedEncodingException: " + e);
							} catch (DOMException e) {
								System.err.println("SOAPTools DOMException: " + e);
							} catch (SOAPException e) {
								System.err.println("SOAPTools SOAPException: " + e);
							}
						   }
                    
					   }
				   }
            
				  
				   return dest;
            
			   }
    
    
    
    
    
    
	/**
	 * erstellt einen Exception Report
	 * @param String exceptionText
	 * @param String exceptionCode
	 * @param String locator
	 * @return SOAPMessage exceptionReportMessage
	 * @throws SOAPException
	 */
	public static SOAPMessage createExceptionReport(String exceptionText, 
	                                                String exceptionCode, 
	                                                String locator,
	                                                boolean createSOAP12) 
	                        throws Exception {
		
		
		      LOGGER.debug("entering createExceptionReport");
		       
		      LOGGER.debug("createExceptionReport  exceptionText: " +  exceptionText);
			  LOGGER.debug("createExceptionReport  exceptionCode: " +  exceptionCode);
			  LOGGER.debug("createExceptionReport  locator: " +  locator);
		
			  
			   
			  

		     
		       
			   //Message exceptionReportMessage = null;
			   
			   SOAPMessage exceptionReportMessage = null;
			   
			   SOAPPart sp = null;
               
               SOAPEnvelope se = null;
		        
        
//		       AxisFault af = new AxisFault("test axis fault");
//		       af.addFaultSubCodeAsString("myfault");
//			   exceptionReportMessage = new Message(af);
             
           	 
				   if (createSOAP12) {
				       
				       System.setProperty("javax.xml.soap.MessageFactory", 
                       "org.apache.axis.soap.MessageFactoryImpl");
   
                        System.setProperty("javax.xml.soap.SOAPFactory", 
                           "org.apache.axis.soap.SOAPFactoryImpl");
				       
				     
				       exceptionReportMessage = new Message(SOAP12ENV, false);
				       
				       sp = (org.apache.axis.SOAPPart) exceptionReportMessage.getSOAPPart();
		               
		               se = (org.apache.axis.message.SOAPEnvelope) sp.getEnvelope();
		            
		             ((org.apache.axis.message.SOAPEnvelope) se).setSoapConstants(SOAPConstants.SOAP12_CONSTANTS);
				       
				       
				   
				   } else {
				       
				       
				       System.setProperty("javax.xml.soap.MessageFactory", 
				               "com.sun.xml.messaging.saaj.soap.MessageFactoryImpl");
   
                        System.setProperty("javax.xml.soap.SOAPFactory", 
                                "com.sun.xml.messaging.saaj.soap.SOAPFactoryImpl");
				       
				       
				       MessageFactory msgFactory = MessageFactory.newInstance();
				       
				       exceptionReportMessage = msgFactory.createMessage();
				       
				      
				       sp = (SOAPPart) exceptionReportMessage.getSOAPPart();
		               
		               se = (javax.xml.soap.SOAPEnvelope) sp.getEnvelope();
				       
				   }
			       
			      
			        
                  // se.clearBody();
			       
				   SOAPBody soapBody = se.getBody();
				   
				   SOAPElement elemExceptionReport = soapBody.addChildElement("ExceptionReport");
			     
				   	   
				   Name nameVersion = se.createName("version");
				   
				   String cswVersion = InitParameters.getCswVersion();
				   
				 
					if (cswVersion == null){
					  
					   cswVersion = "undefined";
					  
					   LOGGER.warn("createExceptionReport: cswVersion is undefined");
				   
					}
				   
					elemExceptionReport.addAttribute(nameVersion, cswVersion );
			   
				   Name nameLanguage = se.createName("language");
				   
				   elemExceptionReport.addAttribute(nameLanguage, "en");
			   
			
			
				   //TODO mehrere Exceptions ?
			
				  SOAPElement elemException = elemExceptionReport.addChildElement( "Exception" );
			 
				  Name nameExceptionCode = se.createName("exceptionCode");
				  elemException.addAttribute(nameExceptionCode, exceptionCode);
			
				  if (locator != null) {
					Name nameLocator = se.createName("locator");
				    elemException.addAttribute(nameLocator, locator);
				  }
				
			 
				  SOAPElement elemExceptionText = elemException.addChildElement( "ExceptionText"); 
				  elemExceptionText.addTextNode(exceptionText);
			 
            
			   
             
		       LOGGER.debug("exiting createExceptionReport");
		   
			   return exceptionReportMessage;
		   }



	/**
			* performs a deep copy of a soap node to dom node
			* @param javax.xml.soap.Node source source node
			* @param  org.w3c.dom.Node dest destination node
			* @return org.w3c.dom.Node dest destination node
			*/
		   public static org.w3c.dom.Node copyNode(javax.xml.soap.Node source,
		   org.w3c.dom.Node dest) {
            
			   
            
			   if (source instanceof javax.xml.soap.Text) {
                
				   String v = ((javax.xml.soap.Text)source).getValue();
                
				   org.w3c.dom.Text tn = dest.getOwnerDocument().createTextNode( v );
				   return tn;
			   } else {
                
				   Iterator attr = ((SOAPElement)source).getAllAttributes();
				   if (attr != null) {
					   while ( attr.hasNext() ) {
						   Name attrName = (Name)attr.next();
						   String name = attrName.getQualifiedName();
						   String value = ((SOAPElement)source).getAttributeValue(attrName);
						
						   try {
							   value = URLDecoder.decode( value , "US-ASCII" );
						   } catch (UnsupportedEncodingException e) {
							 System.out.println(e);
						   }
                        
						   ((org.w3c.dom.Element)dest).setAttribute( name, value );
					   }
				  //TODO nsprefixes
				  /*
				  Iterator nsprefixes = ((SOAPElement)source).getNamespacePrefixes();
					
									  if (nsprefixes != null) {
						
										  while ( nsprefixes.hasNext() ) {
											  String nsPrefix = (String)nsprefixes.next();
											  String nsURI = ((SOAPElement)source).getNamespaceURI(nsPrefix);
							
											  //System.out.println("nsPrefix: " + nsPrefix + "  nsURI: " + nsURI);
							
											  ((org.w3c.dom.Element)dest).setAttribute( "xmlns:" + nsPrefix, nsURI );
							
										  }	
						
						
						 }	
					
				  
				     */
				  
				  
				  
				  
				   }
                
				   Iterator list = ((SOAPElement)source).getChildElements();
                
				   while ( list.hasNext() ) {
                    
					   Object o = list.next();
					   if (o instanceof SOAPElement) {
						   SOAPElement elem = (SOAPElement)o;
                        
						   String name = elem.getElementName().getQualifiedName();
						   org.w3c.dom.Element en = dest.getOwnerDocument().createElement( name );
                        
						   org.w3c.dom.Node n = copyNode( elem, en);
						   dest.appendChild( n );
                        
						   // if node contains a textnode
						   String v = (elem).getValue();
						   if (v != null && v.trim().length() > 0) {
							   try {
							   v = URLDecoder.decode( v , "US-ASCII" );
						   } catch (UnsupportedEncodingException e) {
							 System.out.println(e);
						   }
							   org.w3c.dom.Text tn = dest.getOwnerDocument().createTextNode( v );
							   n.appendChild( tn );
						   }
					   }
                    
				   }
                
			   }
            
			
			   return dest;
             
		   }



}
