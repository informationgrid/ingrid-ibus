/*
 * Created on 29.09.2005
 */
package de.ingrid.ibus.cswinterface.analyse;

import javax.xml.soap.SOAPBodyElement;

import org.w3c.dom.Element;

import de.ingrid.ibus.cswinterface.exceptions.CSWInvalidParameterValueException;
import de.ingrid.ibus.cswinterface.exceptions.CSWMissingParameterValueException;
import de.ingrid.ibus.cswinterface.exceptions.CSWOperationNotSupportedException;

/**
 * @author rschaefer
 *
 */
public class GetRecByIdAnalyser implements CSWAnalyser {

   
    private SessionParameters sessionParameters = null;
    
    
   public GetRecByIdAnalyser(final SessionParameters sessionParams) {
       
       this.sessionParameters = sessionParams;
       
   }
    
    
    /**
     * 
    * @param be SOAPBodyElement
    * @return boolean 
    * @throws Exception e
    * @see de.ingrid.ibus.cswinterface.analyse.CSWAnalyser#analyse(javax.xml.soap.SOAPBodyElement)
    */
    public final boolean analyse(final SOAPBodyElement be) throws Exception {
       
        boolean getRecordByIdRequestValid = false;
         
        String opName = null;
       
        CommonAnalyser commonAnalyser = new CommonAnalyser(this.sessionParameters);
        
        if (be == null) {
            
            throw new Exception("analyse: SOAPBodyElement is null.");
            
        }
        
         
         opName = be.getElementName().getLocalName();
        
         if (opName == null) {
            
             throw new Exception("analyse: opName is null."); 
         }
        
         
         if (!opName.equals("GetRecordById")) {
             
            
             Exception e = 
               new CSWOperationNotSupportedException("Operation " + opName +
                                      " is not supported.", opName); 
             
             throw e; 
         }
         
         
         commonAnalyser.analyseService(be);
         
         
         commonAnalyser.analyseVersion(be);
        
         
         analyseId(be);
         
         
         commonAnalyser.analyseElementSetName(be);
         
       
        
        getRecordByIdRequestValid = true;
        
        return getRecordByIdRequestValid;
        
       
    }
    
    
    /**
     * @param be SOAPBodyElement
     * @return boolean
     * @throws Exception e
     */
    private boolean analyseId(final SOAPBodyElement be) throws Exception {
        
        
        
        Element elemId = (Element) be.getFirstChild();
        
        String ids = null;
        
        if (elemId == null || elemId.getNodeName() != "Id") {
             
            Exception e = 
                new CSWMissingParameterValueException("Element 'Id' is missing.", "Id"); 
            
            throw e;     
        
        } else {
            
            ids = elemId.getFirstChild().getNodeValue();
            
            CommonAnalyser commonAnalyser = new CommonAnalyser(this.sessionParameters);
            
             // tokenize ids: e.g '2, 6, 8'
            
            if (!commonAnalyser.analyseIds(ids)) {
                
                
                Exception e = 
                    new CSWInvalidParameterValueException(" 'Id' is invalid.", "Id"); 
                
                throw e;     
                
            }
            
            
            
           
            
            
        }
     
        return true;
    }   
    
    
    
    /**
     * 
    * @param kvpStr String
    * @return boolean 
    * @throws Exception e
    * @see de.ingrid.ibus.cswinterface.analyse.CSWAnalyser#analyse(java.lang.String)
    */
  
public final boolean analyse(final String kvpStr) throws Exception {
       
       System.out.println("GetRecByIdAnalyser.analyse(kvpStr): Not implemented yet.");
       
       return false;
   }
    

}
