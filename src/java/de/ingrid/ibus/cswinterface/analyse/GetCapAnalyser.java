/*
 * Created on 29.09.2005
 *
 */
package de.ingrid.ibus.cswinterface.analyse;

import javax.xml.soap.SOAPBodyElement;

import java.util.StringTokenizer;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.ingrid.ibus.cswinterface.exceptions.CSWInvalidParameterValueException;
import de.ingrid.ibus.cswinterface.exceptions.CSWMissingParameterValueException;
import de.ingrid.ibus.cswinterface.exceptions.CSWOperationNotSupportedException;
import de.ingrid.ibus.cswinterface.exceptions.CSWVersionNegotiationFailedException;

/**
 * @author rschaefer
 *
 */
public class GetCapAnalyser implements CSWAnalyser {
   
    /**
     * 
    * @param be SOAPBodyElement
    * @return boolean 
    * @throws Exception e
    * @see de.ingrid.ibus.cswinterface.analyse.CSWAnalyser#analyse(javax.xml.soap.SOAPBodyElement)
    */
   public final boolean analyse(final SOAPBodyElement be) throws Exception {
       
      
       boolean getCapRequestValid = false;
       
       String opName = null;
       
       
       CommonAnalyser commonAnalyser = new CommonAnalyser(null);
       
      
       if (be == null) {
          
          throw new Exception("analyse: SOAPBodyElement is null.");
          
      }
      
       opName = be.getElementName().getLocalName();
      
       if (opName == null) {
          
           throw new Exception("analyse: opName is null."); 
       }
       
     
      
   
      if (!opName.equals("GetCapabilities")) {
          
         
          Exception e = 
            new CSWOperationNotSupportedException("Operation " + opName +
                                   " is not supported.", opName); 
          
          throw e; 
      }
      
            
      commonAnalyser.analyseService(be);
      
      
      
      Element elemAccVer = (Element) be.getFirstChild();
      
      
      //if this element is not present, take version 2.0
      if (elemAccVer != null) {
          
          NodeList nl = elemAccVer.getChildNodes();
          
          Element elemVerCurr = null;
          
          String version = null;
          
          boolean versionNegotiationSuccess = false;
          
          int nlLength = nl.getLength();
          
          for (int i = 0; i < nlLength; i++) {
              
              elemVerCurr = (Element) nl.item(i);
              
              //System.out.println("elemVerCurr " + elemVerCurr.getLocalName());
              
              version = elemVerCurr.getFirstChild().getNodeValue();
              
              //System.out.println("version " + version);
              
              if (version.equals("2.0.0")) {
                  
                  versionNegotiationSuccess = true;
                  
                  break;
              }
              
          }
          
          if (!versionNegotiationSuccess) {
              
              Exception e = new CSWVersionNegotiationFailedException("All requested versions are not '2.0.0'.");
              
              throw e;
          }
          
          
      }
      
      
      
      getCapRequestValid = true;
   
      return getCapRequestValid;
   }
    
   
   
   /**
    * 
   * @param kvpStr String
   * @return boolean 
   * @throws Exception e
   * @see de.ingrid.ibus.cswinterface.analyse.CSWAnalyser#analyse(java.lang.String)
   */
  public final boolean analyse(final String kvpStr) throws Exception {
      
      boolean getCapRequestValid = false;
      
     
     
      if (kvpStr == null) {
          
          throw new Exception("analyse: kvpStr is null");
          
      }
      
      //params REQUEST (mandatory: GetCapabilities), SERVICE (mandatory: CSW), ACCEPTVERSIONS (optional: 2.0)
      
     
      if (kvpStr.indexOf("REQUEST") != -1) {
          
          if (kvpStr.indexOf("REQUEST=GetCapabilities") == -1) {
             
              
              Exception e = 
                new CSWInvalidParameterValueException("Value of 'REQUEST' is not 'GetCapabilities'", "REQUEST");
              
              throw e;         
           
          } 
          
      } else {
          
          Exception e = new CSWMissingParameterValueException("Parameter 'REQUEST' is missing.", "REQUEST");
          
          throw e; 
          
      }
      
      
      if (kvpStr.indexOf("SERVICE") != -1) {
          
          if (kvpStr.indexOf("SERVICE=CSW") == -1) {
             
              Exception e = new CSWInvalidParameterValueException("Value of 'SERVICE' is not 'CSW'", "SERVICE");
              
              throw e;         
           
          } 
          
      } else {
          
         
          Exception e = new CSWMissingParameterValueException("Parameter 'SERVICE' is missing.", "SERVICE");
          
          throw e; 
          
      }
      
      
      
      if (kvpStr.indexOf("ACCEPTVERSIONS") != -1) {
          
          if (kvpStr.indexOf("ACCEPTVERSIONS=2.0.0") == -1) {
             
              
              Exception e = new CSWVersionNegotiationFailedException("Value of 'ACCEPTVERSIONS' is not '2.0.0'");
              
              throw e;         
           
          } 
          
      } //if ACCEPTVERSIONS is missing take version 2.0
      
      
      
      
      if (kvpStr.matches("REQUEST=GetCapabilities&SERVICE=CSW") || 
          kvpStr.matches("SERVICE=CSW&REQUEST=GetCapabilities") ||
          kvpStr.matches("ACCEPTVERSIONS=2.0.0&REQUEST=GetCapabilities&SERVICE=CSW") ||
          kvpStr.matches("ACCEPTVERSIONS=2.0.0&SERVICE=CSW&REQUEST=GetCapabilities") ||
          kvpStr.matches("SERVICE=CSW&ACCEPTVERSIONS=2.0.0&REQUEST=GetCapabilities") ||
          kvpStr.matches("SERVICE=CSW&REQUEST=GetCapabilities&ACCEPTVERSIONS=2.0.0") ||
          kvpStr.matches("REQUEST=GetCapabilities&SERVICE=CSW&ACCEPTVERSIONS=2.0.0") ||
          kvpStr.matches("REQUEST=GetCapabilities&ACCEPTVERSIONS=2.0.0&SERVICE=CSW")
          ) {
               
           
            getCapRequestValid = true;   
         
           
      
      
      }
      
      
      
      return getCapRequestValid;
  }
   

}
