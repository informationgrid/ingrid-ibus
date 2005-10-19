/*
 * Created on 29.09.2005
 *
 */
package de.ingrid.ibus.cswinterface.analyse;

import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.ingrid.ibus.cswinterface.exceptions.CSWInvalidParameterValueException;
import de.ingrid.ibus.cswinterface.exceptions.CSWMissingParameterValueException;
import de.ingrid.ibus.cswinterface.exceptions.CSWOperationNotSupportedException;


/**
 * @author rschaefer
 * 
 */
public class GetRecAnalyser implements CSWAnalyser {

   
   private SessionParameters sessionParameters = null;
    
    
   public GetRecAnalyser(final SessionParameters sessionParams) {
       
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
       
        boolean getRecordsRequestValid = false;
       
        String opName = null;
       
        String startPosition = null;
        
        int startPositionInt = 0;
        
        String maxRecords = null;
        
        int maxRecordsInt = 0;
        
        CommonAnalyser commonAnalyser = new CommonAnalyser(this.sessionParameters);
        
        
               
        if (be == null) {
            
            throw new Exception("analyse: SOAPBodyElement is null.");
            
        }
        
         
         opName = be.getElementName().getLocalName();
        
         if (opName == null) {
            
             throw new Exception("analyse: opName is null."); 
         }
        
         
         if (!opName.equals("GetRecords")) {
             
            
             Exception e = 
               new CSWOperationNotSupportedException("Operation " + opName +
                                      " is not supported.", opName); 
             
             throw e; 
         }
         
         
         commonAnalyser.analyseService(be);
         
         commonAnalyser.analyseVersion(be);
        
         commonAnalyser.analyseResultType(be);
      
         commonAnalyser.analyseOutputFormat(be);
         
         commonAnalyser.analyseOutputSchema(be);
         
    
         startPosition = be.getAttribute("startPosition");
         
         if (startPosition != null) {
             
             startPositionInt = Integer.parseInt(startPosition);
             
             if (startPositionInt < 1) {
                 
                 Exception e = 
                     new CSWInvalidParameterValueException("Attribute 'startPosition' is invalid.", "startPosition"); 
                 
                 throw e;     
                 
                 
             } else {
                 
                 sessionParameters.setStartPosition(startPositionInt);  
             }
             
             
         }
         
         
         
         maxRecords = be.getAttribute("maxRecords");
         
         if (maxRecords != null) {
             
             maxRecordsInt = Integer.parseInt(maxRecords);
             
             if (maxRecordsInt < 1) {
                 
                 Exception e = 
                     new CSWInvalidParameterValueException("Attribute 'maxRecords' is invalid.", "maxRecords"); 
                 
                 throw e;     
                 
                 
             } else {
                 
                 sessionParameters.setMaxRecords(maxRecordsInt);  
             }
             
             
         }
         
  
         analyseQuery(be);
         
        
         commonAnalyser.analyseElementSetName(be);
         
         
         
         analyseConstraint(be);
         
         
         analyseFilter(be);
         
      
        getRecordsRequestValid = true;
         
        return getRecordsRequestValid;
       
    }
    
    
  /**
 * @return queryIsValid boolean 
 * @throws Exception e
 */
private boolean analyseQuery(final SOAPBodyElement be) throws Exception {
       
       boolean queryIsValid = false;
       
       Element elemQuery = (Element) be.getFirstChild();
       
       String typeNames = null;
       
       if (elemQuery == null || elemQuery.getNodeName() != "Query") {
           
          
           Exception e = 
               new CSWMissingParameterValueException("Element 'Query' is missing.", "Query"); 
           
           throw e;     
       
       }
       
       
       typeNames = elemQuery.getAttribute("typeNames");
       
       
       if (typeNames == null) {
           
           Exception e = 
               new CSWMissingParameterValueException("Attribute 'typeNames' is missing.", "typeNames"); 
           
           throw e;       
           
           
       } else {
           
        //analyse typeNames: csw:dataset,csw:datasetcollection,csw:service,csw:application
           
           CommonAnalyser commonAnalyser = new CommonAnalyser(this.sessionParameters);
           
           if (!commonAnalyser.analyseTypeNames(typeNames)) {
               
               Exception e = 
                   new CSWInvalidParameterValueException("Attribute 'typeNames' is invalid.", "typeNames"); 
               
               throw e;     
               
           }
            
           
       }
       
       
       return queryIsValid;
       
   }
    


/**
 * @param be SOAPBodyElement
 * @return boolean
 * @throws Exception e
 */
private boolean analyseConstraint(final SOAPBodyElement be) throws Exception {
    
    Element elemConstraint = null;
    
    String constraintLangVersion = null;
    
    NodeList nl = be.getElementsByTagName("Constraint");
    
     
    if (nl != null || nl.getLength() != 0) {
        
        elemConstraint = (Element) nl.item(0);
       
    } 
    
    if (elemConstraint != null) {
        
        constraintLangVersion = elemConstraint.getAttribute("version");
        
    } else {
        
        Exception e = 
            new CSWMissingParameterValueException("Element 'Constraint' is missing.", "Constraint"); 
        
        throw e; 
         
    }
    
    
    if (constraintLangVersion != null) {
        
        //allow only Filter encoding 1.0.0 
        if (!constraintLangVersion.equals("1.0.0")) {
            
            Exception e = 
                new CSWInvalidParameterValueException("Attribute 'version' of Element 'Constraint' is not '1.0.0'.", 
                                                  "version"); 
            
            throw e; 
            
        }
        
        
    } else {
        
        Exception e = 
            new CSWMissingParameterValueException("Attribute 'version' of Element 'Constraint' is missing.", 
                                                   "version"); 
        
        throw e; 
        
    }
    
    
    return true;
}


/**
 * Allow only OGC-Filter. CqlText is not supported yet.
 * @param be
 * @return
 * @throws Exception
 */
private boolean analyseFilter(final SOAPBodyElement be) throws Exception {
    
    SOAPElement soapElementFilter = null;
    
    NodeList nl = be.getElementsByTagName("Filter");
    
    if (nl != null || nl.getLength() != 0) {
    
        soapElementFilter = (SOAPElement) nl.item(0);
               
    }
    
    if (soapElementFilter != null) {
        
        sessionParameters.setSoapElementFilter(soapElementFilter);
    
    } else {
        
        Exception e = 
            new CSWMissingParameterValueException("Element 'Filter' is missing.", "Filter"); 
        
        throw e; 
        
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
       
       System.out.println("GetRecAnalyser.analyse(kvpStr): Not implemented yet.");
       
       return false;
   }
    

}
