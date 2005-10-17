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
 * Rundeturmstr. 12                                                           *
 * D-64283 Darmstadt                                                          *
 * info@gistec-online.de                          http://www.gistec-online.de *
 *----------------------------------------------------------------------------*
 *                                                                            *
 * Copyright © 2005 GIStec GmbH                                               *
 * ALL Rights Reserved.                                                       *
 *                                                                            *
 *+---------------------------------------------------------------------------*
 *                                                                            *
 * Author           : Ralf Schäfer                                            *
 * Erstellungsdatum : 28.10.2005                                              *
 * Version          : 1.0                                                     *
 * Beschreibung     : Servlet fuer die InGrid 1.0 CSW-Schnittstelle           *
 *                                                                            *
 *                                                                            *
 *----------------------------------------------------------------------------*
 * Änderungen (Datum, Version, Author, Beschreibung)                          *
 *----------------------------------------------------------------------------*
 *            |         |          |                                          *
 *            |         |          |                                          *
 *----------------------------------------------------------------------------*
*/

package de.ingrid.ibus.cswinterface;


//IMPORTS java.io
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.messaging.JAXMServlet;
import javax.xml.messaging.ReqRespListener;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.axis.Message;
import org.apache.axis.SOAPPart;
import org.apache.axis.message.SOAPBody;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.soap.SOAP12Constants;
import org.apache.axis.soap.SOAPConstants;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;

import de.ingrid.ibus.cswinterface.analyse.GetCapAnalyser;
import de.ingrid.ibus.cswinterface.analyse.SessionParameters;
import de.ingrid.ibus.cswinterface.exceptions.CSWException;
import de.ingrid.ibus.cswinterface.tools.AxisTools;
import de.ingrid.ibus.cswinterface.tools.PatternTools;
import de.ingrid.ibus.cswinterface.tools.SOAPTools;
import de.ingrid.ibus.cswinterface.tools.XMLTools;




/**
 * Servlet fuer den InGeo Catalog Service
 * nimmt SOAP-Anfrage entgegen und schickt
 * SOAP-Response zurueck
 * @author rschaefer
 */
public class CSWServlet extends JAXMServlet implements ReqRespListener {

	
   
    /**
     * Comment for <code>serialVersionUID</code>
     */
    static final long serialVersionUID = 0;
	
	
    /**
	  *  der Catalog Service 
	  */
	   //private InGeoWCAS wcas = null;
    
      
	   
	   //TODO log with commons logging
	   private final Logger logger = Logger.getLogger(this.getClass());
	   
	    
	   
	   /**
		 * Init-Methode des Servlets. Wird nur einmal beim Start der Webapplikation aufgerufen.
		 * @param servletConfig servlet configuration
		 * @throws ServletException exception
		 */
		public final void init(final ServletConfig servletConfig) throws ServletException {
			
			super.init(servletConfig);
			
			
			//TODO init mit jConfig? 
	     
	         
		     //debugging mit log4j
			String log4jPropertiesFile = getInitParameter("log4jPropertiesFile");
			
			InitParameters.setLog4jPropertiesFile(log4jPropertiesFile);
			
			 //log4j logs with configuration file 
			 PropertyConfigurator.configure(log4jPropertiesFile); 
			
			logger.debug("entering init(ServletConfig servletConfig)");
			
			
	         //System.out.println("init: capabilitiesFile is " + InitParameters.getCapabilitiesFile());
		     logger.debug("init: log4jPropertiesFile is " + InitParameters.getLog4jPropertiesFile());

	       
	       
	        //	TODO Pfade pruefen mit FileOutputStream 
			
			//fuer XML/XSL-Dateien absolute Pfade zusammensetzen 
				   String absPathBeg = "file:///";
	  
			       String cswVersion = getInitParameter("cswversion");
			    
			       if  (!PatternTools.isVersionFormatValid(cswVersion)) { 
						throw new ServletException("Init parameter 'wcas_version' has not the correct format: " + 
						                            cswVersion);
	               } else {
	               	
					InitParameters.setCswVersion(cswVersion);
	               	
					System.out.println("Starting CSW  Version " + InitParameters.getCswVersion());
					
					logger.info("Starting CSW  Version " + InitParameters.getCswVersion());
					
					
	               
	               }
	               
				   String capabilitiesFile = getInitParameter("capabilities");
				   capabilitiesFile = absPathBeg + getServletConfig().getServletContext().getRealPath(capabilitiesFile);
			       
			       InitParameters.setCapabilitiesFile(capabilitiesFile);
			       
			   //System.out.println("init: capabilitiesFile is " + InitParameters.getCapabilitiesFile());
				logger.debug("init: capabilitiesFile is " + InitParameters.getCapabilitiesFile());
			      

			  
			     String strMaxRecords =  getInitParameter("maxRecords");
			     
			     InitParameters.setMaxRecords(Integer.parseInt(strMaxRecords));
			     
	            //System.out.println("init: maxRecords is " + InitParameters.getMaxRecords());
		        logger.debug("init: maxRecords is " + InitParameters.getMaxRecords());
			     
			     
			     //Koordinaten Gesamtdeutschland als Strings
			     InitParameters.setDefault_WEST_Coord(getInitParameter("default_WEST_Coord"));
			     
	              //System.out.println("init: default_WEST_Coord is " + InitParameters.getDefault_WEST_Coord());
			     logger.debug("init: default_WEST_Coord is " + InitParameters.getDefault_WEST_Coord());
			     
			     
			     
			     InitParameters.setDefault_OST_Coord(getInitParameter("default_OST_Coord"));
			     
			   //System.out.println("init: default_OST_Coord is " + InitParameters.getDefault_OST_Coord());
			   logger.debug("init: default_OST_Coord is " + InitParameters.getDefault_OST_Coord());
			     
			     
			     
			     InitParameters.setDefault_NORD_Coord(getInitParameter("default_NORD_Coord"));
			     
			   //System.out.println("init: default_NORD_Coord is " + InitParameters.getDefault_NORD_Coord());
			   logger.debug("init: default_NORD_Coord is " + InitParameters.getDefault_NORD_Coord());  
			     
			     
			     
			   InitParameters.setDefault_SUED_Coord(getInitParameter("default_SUED_Coord"));
			     
			     
			    //System.out.println("init: default_SUED_Coord is " + InitParameters.getDefault_SUED_Coord());
			   logger.debug("init: default_SUED_Coord is " + InitParameters.getDefault_SUED_Coord());
			     
			     
			    logMemoryInfo("init"); 
			     
			    logger.debug("exiting init(ServletConfig servletConfig)");
		
		}


	   
	   


	/**
     * Diese Methode wird von der Servlet Engine aufgerufen, wenn ein SOAP-Request
     * ankommt.
     * Der Rueckgabewert mess wird an den Client geschickt.
     * @param soapRequestMessage SOAPMessage
     * @return soapResponseMessage SOAPMessage
     */
    public final SOAPMessage onMessage(final SOAPMessage soapRequestMessage) {
        
		System.out.println("CSWServlet: entering onMessage");
		
		//logger.debug("entering onMessage");
		
        //Startzeit aufnehmen
	    long startTime = System.currentTimeMillis();
	    
		//logger.debug("onMessage start time: " + startTime);
	    
	    
	
	    //SOAPMessage soapResponseMessage = null;
	    
        Message soapResponseMessage = null;
        
        
		

		
//		System.setProperty("javax.xml.parsers.DocumentBuilderFactory", 
//		                   "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
	
		System.setProperty("org.xml.sax.parser", "org.apache.xerces.parsers.SAXParser");
		
//		System.setProperty("javax.xml.transform.TransformerFactory", 
//		                   "org.apache.xalan.processor.TransformerFactoryImpl");
		
		
		//log  received message / request
		try {
                    
          //message.writeTo(System.out);
          
		  ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		  
		  soapRequestMessage.writeTo(byteArrayOutputStream);
		  
		  System.out.println("onMessage: received message: " + byteArrayOutputStream.toString());
          
          
		  logger.debug("onMessage: received message: " + byteArrayOutputStream.toString());
		  
        
        } catch (SOAPException e) {
			
			System.err.println("CSWServlet onMessage(): SOAPException: " + e);
			 
			logger.error("onMessage: " + e, e);
        
        } catch (IOException e) {
        	
			 System.err.println("CSWServlet onMessage(): IOException: " + e);
			 
			 logger.error("onMessage: " + e, e);
        }
	    
	
		
		try {
			
		    //test if SOAP is version 1.2
		   
         if (!AxisTools.isSOAP12((Message) soapRequestMessage)) {
             
             Exception e = new Exception("SOAP version is not SOAP 1.2.");
             
             throw e;
             
             //TODO send SOAP 1.1 response?
         }
		  
         
            //wcas = new InGeoWCAS();
         
			//soapResponseMessage = wcas.performMessage( soapRequestMessage );
			
		    //FIXME only for testing
		    soapResponseMessage = (Message) soapRequestMessage;
		    
         /*
            soapResponseMessage = new Message(TestRequests.GETCAP1, false);
		    
		    SOAPPart sp = (SOAPPart) soapResponseMessage.getSOAPPart();
	        
	        SOAPEnvelope se = (SOAPEnvelope) sp.getEnvelope();
	        
	        se.clearBody();
	        
	        SOAPBody body = (SOAPBody) se.getBody();

	        URL url = new URL(InitParameters.getCapabilitiesFile());
	        
	        Document doc = XMLTools.parse(url.getPath());
	        
	        //System.out.println("doc: " + doc.getDocumentElement().toString());
            
	        body.addDocument(doc);
	       
	        //set SOAP Version 
	        se.setSoapConstants(SOAPConstants.SOAP12_CONSTANTS);
	        //se.setSoapConstants(SOAPConstants.SOAP11_CONSTANTS);
	        
	       */
		  
//		    Exception e = new Exception("test exception");
//            
//            throw e;
		    
		
		} catch (Exception e) {
			
			System.err.println("CSWServlet onMessage(): Exception: " + e);
			
			logger.error("onMessage: " + e, e);
			
			if (e instanceof CSWException) {
			   
			  try {
			  	
			     soapResponseMessage = SOAPTools.createExceptionReport(getClass().getName() + ": " + e.getMessage() , 
				                                      ((CSWException) e).getExceptionCode(), 
				                                       ((CSWException) e).getLocator());
			  } catch (SOAPException e1) {
			  	
				System.err.println("CSWServlet onMessage(): SOAPException: " + e1);
			 
				logger.error("onMessage: " + e1, e1);
				
			  }
			  
			} else {
				 
			 try {
			 						 											 
			     soapResponseMessage = SOAPTools.createExceptionReport(getClass().getName() + ": " + e.toString(), 
													    "NoApplicableCode", 
													    null);
			   } catch (SOAPException e1) {
				
				System.err.println("CSWServlet onMessage(): SOAPException: " + e1);
			
				logger.error("onMessage: " + e1, e1);
			  }
			 
			} 
		}
		
	 
        //log  outgoing message / response
	     try {
	     	
            //mess.writeTo(System.out);
	     	
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	     	
			soapResponseMessage.writeTo(byteArrayOutputStream);
            
           System.out.println("onMessage: outgoing message: " + byteArrayOutputStream.toString());
          
          
		   logger.debug("onMessage: outgoing message: " + byteArrayOutputStream.toString());
            
        
		 } catch (SOAPException e) {
			
			 System.err.println("CSWServlet onMessage(): SOAPException: " + e);
			 
			 logger.error("onMessage: " + e, e);
        
		 } catch (IOException e) {
        	
			  System.err.println("CSWServlet onMessage(): IOException: " + e);
			 
			  logger.error("onMessage: " + e, e);
		 }
		  
	  logMemoryInfo("onMessage");
	 
      long elapsedTime = System.currentTimeMillis() - startTime;	
		
	  System.out.println("CSWServlet: exiting onMessage: finished response in " + elapsedTime + " ms");
		
	  logger.debug("exiting onMessage: finished response in " + elapsedTime + " ms");	
	

	  return soapResponseMessage;
	  
	}
	
	
	

	
	
	
	
  
   
	
	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
    public final void doGet(final HttpServletRequest request, final HttpServletResponse response) {
    
        final String methodName = "doGet";
        
        logger.info("entering " + methodName);
        
        System.out.println("CSWServlet: entering " + methodName);
        
        
        
        //SessionParameters.clear();
        
        //sessionParameters = new SessionParameters();
        
        try {
           
          
            
//            String paramRequestValue = request.getParameter("request");
//            
//            System.out.println("CSWServlet: got REQUEST: " + paramRequestValue); 
//
//            if(paramRequestValue != null && paramRequestValue.equals("GetCapabilities")) {
//              
//                System.out.println("CSWServlet: got a 'getCapabilities' request");
//                
//            }
            
            String queryString = request.getQueryString();
            
            //System.out.println("CSWServlet:  queryString: " + queryString); 
            
            //TODO consider other REQUESTs
            
            GetCapAnalyser getCapAnalyser = new GetCapAnalyser();
            
            //TODO catch all exceptions
            if (getCapAnalyser.analyse(queryString)) {
                
                  
                response.setContentType("text/xml");
                
                
                URL url = new URL(InitParameters.getCapabilitiesFile());
                
               
                //TODO remove comment; test 
               //( sendURL(url, response);

                writeInput2OutputStream(url.openStream(), response.getOutputStream());
                
            
            
             //TODO send exceptions
            } else {
            
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                
            }
            
            
           
            
           
            
        
        } catch (Exception e) {
           
            System.err.println("CSWServlet " + methodName + ": " + e);
			
			logger.error(methodName + ": " + e, e);
            
        }
    
        logger.info("exiting " + methodName);
        
        System.out.println("CSWServlet: exiting " + methodName); 
        
    
    }
    

    
    
    

    
    /**
     * 
     * TODO put in another class
     * 
     * @param inputStream InputStream
     * @param outputstream OutputStream
     * @throws IOException e
     */
    private void writeInput2OutputStream(final InputStream inputStream, final OutputStream outputstream) 
              throws IOException {
        
         int c = 0;
        
         Reader reader = null;
      
         try {
          
         
            reader = new InputStreamReader(inputStream);
            
            
            
            while ((c = reader.read()) != -1) {
  	       
               outputstream.write(c);     
            } 
         
            
         } finally {
           
             if (reader != null) {
  	     
                 reader.close();    
           }	    
        }	     
  	    
  	       
     }	
    

  /**
   * logs JVM memory information. 
   * Parameter string strMethodName is the name of 
   * the method where the logging occurs.
   * @param strMethodName String name of the method 
   */
  private void logMemoryInfo(final String strMethodName) {
	
        //	get some information about memory
		Runtime runtime = Runtime.getRuntime();
		  
	    long longTotalMemory  = runtime.totalMemory();
	      
	    //System.out.println(strMethodName + ": JVM total memory: " +  Long.toString(longTotalMemory));
	    logger.debug(strMethodName + ": JVM total memory: " +  Long.toString(longTotalMemory));
	
		long longFreeMemory = runtime.freeMemory();
		
	    //System.out.println(strMethodName + ": JVM free memory: " +  Long.toString(longFreeMemory));
	    logger.debug(strMethodName + ": JVM free memory: " +  Long.toString(longFreeMemory));
		 
	    long longMaxMemory = runtime.maxMemory();	
	    
	    //System.out.println(strMethodName + ": JVM max memory: " +  Long.toString(longMaxMemory));
	    logger.debug(strMethodName  + ": JVM max memory: " +  Long.toString(longMaxMemory));
	
	
  }

	

}
