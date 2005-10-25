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
import javax.xml.soap.SOAPElement;
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
import de.ingrid.ibus.cswinterface.exceptions.CSWException;
import de.ingrid.ibus.cswinterface.tools.AxisTools;
import de.ingrid.ibus.cswinterface.tools.IOTools;
import de.ingrid.ibus.cswinterface.tools.SOAPTools;





/**
 * servlet for the CSW.
 * receives a SOAP request and returns 
 * a SOAP response.
 * @author rschaefer
 */
public class CSWServlet extends JAXMServlet implements ReqRespListener {

	
   
    /**
     * the serialVersionUID
     */
    static final long serialVersionUID = 0;
	
	
    /**
	  *  the Catalogue Service Web 
	  */
	   private CSW csw = null;
	   
	   private boolean createSOAP12 = true;
    
      
	   
	   //TODO log with commons logging
	   private final Logger logger = Logger.getLogger(this.getClass());
	   
	    
	   
	   /**
		 * Init-Methode des Servlets. Wird nur einmal beim Start der Webapplikation aufgerufen.
		 * @param servletConfig servlet configuration
		 * @throws ServletException exception
		 */
 public final void init(final ServletConfig servletConfig) throws ServletException {
     
   
        super.init(servletConfig);

        //debugging mit log4j
        String log4jPropertiesFile = getInitParameter("log4jPropertiesFile");

        InitParameters.setLog4jPropertiesFile(log4jPropertiesFile);

        //log4j logs with configuration file
        PropertyConfigurator.configure(log4jPropertiesFile);

        logger.debug("entering init(ServletConfig servletConfig)");

   
        System.setProperty("javax.xml.soap.MessageFactory", 
          "org.apache.axis.soap.MessageFactoryImpl");

        System.setProperty("javax.xml.soap.SOAPFactory", 
            "org.apache.axis.soap.SOAPFactoryImpl");
        
        
        System.out.println( "javax.xml.soap.MessageFactory: " + System.getProperty("javax.xml.soap.MessageFactory") );
        
        System.out.println("javax.xml.soap.SOAPFactory: " + System.getProperty("javax.xml.soap.SOAPFactory") );
        
        
        
        //System.out.println("init: capabilitiesFile is " +
        // InitParameters.getCapabilitiesFile());
        logger.info("init: log4jPropertiesFile is " + InitParameters.getLog4jPropertiesFile());

        //	TODO Pfade pruefen mit FileOutputStream

        //fuer XML/XSL-Dateien absolute Pfade zusammensetzen
        String absPathBeg = "file:///";

        
        System.out.println("Starting CSW  Version " + InitParameters.getCswVersion());

        logger.info("Starting CSW  Version " + InitParameters.getCswVersion());

        String capabilitiesFile = getInitParameter("capabilities");
        capabilitiesFile = absPathBeg + getServletConfig().getServletContext().getRealPath(capabilitiesFile);

        InitParameters.setCapabilitiesFile(capabilitiesFile);

        //System.out.println("init: capabilitiesFile is " +
        // InitParameters.getCapabilitiesFile());
        logger.info("init: capabilitiesFile is " + InitParameters.getCapabilitiesFile());

        String describeRecordFile = getInitParameter("describerecord");
        describeRecordFile = absPathBeg + getServletConfig().getServletContext().getRealPath(describeRecordFile);

        InitParameters.setDescribeRecordFile(describeRecordFile);

        //System.out.println("init: capabilitiesFile is " +
        // InitParameters.getCapabilitiesFile());
        logger.info("init: describeRecordFile is " + InitParameters.getDescribeRecordFile());

        String strMaxRecords = getInitParameter("maxRecords");

        InitParameters.setMaxRecords(Integer.parseInt(strMaxRecords));

        //System.out.println("init: maxRecords is " +
        // InitParameters.getMaxRecords());
        logger.info("init: maxRecords is " + InitParameters.getMaxRecords());

        //Koordinaten Gesamtdeutschland als Strings
        InitParameters.setDefault_WEST_Coord(getInitParameter("default_WEST_Coord"));

        //System.out.println("init: default_WEST_Coord is " +
        // InitParameters.getDefault_WEST_Coord());
        logger.info("init: default_WEST_Coord is " + InitParameters.getDefault_WEST_Coord());

        InitParameters.setDefault_OST_Coord(getInitParameter("default_OST_Coord"));

        //System.out.println("init: default_OST_Coord is " +
        // InitParameters.getDefault_OST_Coord());
        logger.info("init: default_OST_Coord is " + InitParameters.getDefault_OST_Coord());

        InitParameters.setDefault_NORD_Coord(getInitParameter("default_NORD_Coord"));

        //System.out.println("init: default_NORD_Coord is " +
        // InitParameters.getDefault_NORD_Coord());
        logger.info("init: default_NORD_Coord is " + InitParameters.getDefault_NORD_Coord());

        InitParameters.setDefault_SUED_Coord(getInitParameter("default_SUED_Coord"));

        //System.out.println("init: default_SUED_Coord is " +
        // InitParameters.getDefault_SUED_Coord());
        logger.info("init: default_SUED_Coord is " + InitParameters.getDefault_SUED_Coord());

        logMemoryInfo("init");

        logger.debug("exiting init(ServletConfig servletConfig)");

    }


	   
	   


	/**
     * Diese Methode wird von der Servlet Engine aufgerufen, wenn ein
     * SOAP-Request ankommt. Der Rueckgabewert mess wird an den Client
     * geschickt.
     * 
     * @param message
     *            SOAPMessage
     * @return soapResponseMessage SOAPMessage
     */
    public final SOAPMessage onMessage(final SOAPMessage message) {
        
		String methodName = "onMessage";
        
        System.out.println("CSWServlet: entering " + methodName);
        
	    logger.debug("entering " + methodName);
	    
	    //Startzeit aufnehmen
	    long startTime = System.currentTimeMillis();
	    //logger.debug("onMessage start time: " + startTime);
	    
	    
//		System.setProperty("javax.xml.parsers.DocumentBuilderFactory", 
//        "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");

//System.setProperty("org.xml.sax.parser", "org.apache.xerces.parsers.SAXParser");

//System.setProperty("javax.xml.transform.TransformerFactory", 
//        "org.apache.xalan.processor.TransformerFactoryImpl");
	    
	    
	    
	    System.setProperty("javax.xml.soap.MessageFactory", 
            "org.apache.axis.soap.MessageFactoryImpl");

            System.setProperty("javax.xml.soap.SOAPFactory", 
              "org.apache.axis.soap.SOAPFactoryImpl");
	    
	    
	    createSOAP12 = true;
	    
	    Message soapRequestMessage = (Message) message;
	    
	    //Message soapResponseMessage = null;
	    
	    SOAPMessage soapResponseMessage = null;
	    
		
	    try {
	   		
		//log  received message / request
               
          //soapRequestMessage.writeTo(System.out);
          System.out.println(methodName + ": received message: " + soapRequestMessage.getSOAPPartAsString());
          
          logger.debug(methodName + ": received message: " + soapRequestMessage.getSOAPPartAsString());
		  
        
       		
		    //test if SOAP is version 1.2
		   
         if (!AxisTools.isSOAP12((Message) soapRequestMessage)) {
             
             Exception e = new Exception("SOAP version of request is not SOAP 1.2.");
             
             //TODO why is SOAP 1.1 not working?
             //It seems com.sun.xml.messaging.saaj.soap.*Impl make a mess?!
             
             //createSOAP12 = false;
             
             throw e;
             
             //TODO send SOAP 1.1 response?
         }
		  
           csw = new CSW();
         
		   soapResponseMessage = csw.doRequest((Message) soapRequestMessage);
		
	
      
    } catch (Exception e) {
		
		System.err.println(methodName + ": Exception: " + e);
		
		logger.error(methodName + ": " + e, e);
		
		 try {
				
		     
				if (e instanceof CSWException) {
				   
				 
				  	
				     soapResponseMessage = SOAPTools.createExceptionReport(getClass().getName() + 
				                                           ": " + e.getMessage() , 
					                                      ((CSWException) e).getExceptionCode(), 
					                                       ((CSWException) e).getLocator(), createSOAP12);
				 
				  
				} else {
					 
			
				 						 											 
				     soapResponseMessage = SOAPTools.createExceptionReport(getClass().getName() + 
				                                            ": " + e.toString(), 
														    "NoApplicableCode", 
														    null, createSOAP12);
				  
				 
				  } 
				 
				
			   } catch (Exception se) {
						
			         System.err.println(methodName + ": Exception: " + se);
					
					 logger.error(methodName + ": " + se, se);
			   } 
		
		
    }	
	  
    try {
   
    
       
        if (soapResponseMessage == null) {
            
            Exception e = new Exception("SOAP response is null.");
            
            soapResponseMessage = SOAPTools.createExceptionReport(getClass().getName() + 
                    ": " + e.toString(), 
				    "NoApplicableCode", 
				    null, true);
            
            throw e;
        }
        
        //log  outgoing message / response
        
       //soapResponseMessage.writeTo(System.out);
    
       //System.out.println("onMessage: outgoing message: " + soapResponseMessage.getSOAPPartAsString());
       
       //logger.debug("onMessage: outgoing message: " + soapResponseMessage.getSOAPPartAsString());
    
       
    } catch (Exception se) {
		
       System.err.println(methodName + ": Exception: " + se);
	
	   logger.error(methodName + ": " + se, se);
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
            
            response.setContentType("text/xml");
            
            //TODO consider other REQUESTs e.g. DESCRIBERECORD
            
              GetCapAnalyser getCapAnalyser = new GetCapAnalyser();
            
         
               if (getCapAnalyser.analyse(queryString)) {
                
                     URL url = new URL(InitParameters.getCapabilitiesFile());
                
                     IOTools.writeInputToOutputStream(url.openStream(), response.getOutputStream());
               
           
            } /*else {
            
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                
            }
            */
            
           
            
           
            
        
        } catch (Exception e) {
            
            
            System.err.println(methodName + ": Exception: " + e);
    		
    		logger.error(methodName + ": " + e, e);
    		
    		 try {
    				
    		      SOAPMessage  soapResponseMessage = null;
    				     
    				if (e instanceof CSWException) {
    				   
    				 
    				  	
    				     soapResponseMessage = SOAPTools.createExceptionReport(getClass().getName() +
    				                                                   ": " +  e.getMessage() , 
    					                                      ((CSWException) e).getExceptionCode(), 
    					                                       ((CSWException) e).getLocator(), createSOAP12);
    				 
    				  
    				} else {
    					 
    			
    				 						 											 
    				     soapResponseMessage = SOAPTools.createExceptionReport(getClass().getName() + 
    				                                                     ": " + e.toString(), 
    														           "NoApplicableCode", 
    														           null, createSOAP12);
    				  
    				 
    				  } 
    				
    				  //put SOAP XML into the response object 
                      soapResponseMessage.writeTo(response.getOutputStream());
                       
    				
    			   } catch (Exception se) {
    						
    			         System.err.println(methodName + ": Exception: " + se);
    					
    					 logger.error(methodName + ": " + se, se);
    			   } 
    		
           
            
            
        }
    
        
        
        
        logger.info("exiting " + methodName);
        
        System.out.println("CSWServlet: exiting " + methodName); 
        
    
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
