/*
 * Created on 29.09.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.ibus.cswinterface.analyse;

import javax.xml.soap.SOAPBodyElement;

import de.ingrid.ibus.cswinterface.exceptions.CSWOperationNotSupportedException;

/**
 * @author rschaefer
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DescRecAnalyser implements CSWAnalyser {

    /**
     * 
    * @param be SOAPBodyElement
    * @return boolean 
    * @throws Exception e
    * @see de.ingrid.ibus.cswinterface.analyse.CSWAnalyser#analyse(javax.xml.soap.SOAPBodyElement)
    */
    public final boolean analyse(final SOAPBodyElement be) throws Exception {
       
         boolean descRecRequestValid = false;
        
         String opName = null;
        
         CommonAnalyser commonAnalyser = new CommonAnalyser(null);
         
         if (be == null) {
             
             throw new Exception("analyse: SOAPBodyElement is null.");
             
         }
         
          
          opName = be.getElementName().getLocalName();
         
          if (opName == null) {
             
              throw new Exception("analyse: opName is null."); 
          }
         
          
          if (!opName.equals("DescribeRecord")) {
              
             
              Exception e = 
                new CSWOperationNotSupportedException("Operation " + opName +
                                       " is not supported.", opName); 
              
              throw e; 
          }
          
          
          commonAnalyser.analyseService(be);
          
          commonAnalyser.analyseVersion(be);
     
          commonAnalyser.analyseOutputFormat(be);
          
          commonAnalyser.analyseSchemaLanguage(be);
        
        
          descRecRequestValid = true;
        
          return descRecRequestValid;
       
    }
    
    
    /**
     * 
    * @param kvpStr String
    * @return boolean 
    * @throws Exception e
    * @see de.ingrid.ibus.cswinterface.analyse.CSWAnalyser#analyse(java.lang.String)
    */
  
public final boolean analyse(final String kvpStr) throws Exception {
       
       System.out.println("DescRecAnalyser.analyse(kvpStr): Not implemented yet.");
       
       return false;
   }
    

}
