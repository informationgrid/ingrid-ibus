/*
 * Created on 12.10.2005
 *
 */
package de.ingrid.ibus.cswinterface.analyse;

import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.xml.soap.SOAPBodyElement;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.ingrid.ibus.cswinterface.exceptions.CSWInvalidParameterValueException;
import de.ingrid.ibus.cswinterface.exceptions.CSWMissingParameterValueException;

/**
 * This class does an analysis of different 
 * elements of OGC requests and puts some of
 * the values into the sessionParameters.
 * @author rschaefer
 *
 */
public final class CommonAnalyser {
    
    /**
     * stores some values of the requests
     */
    private SessionParameters sessionParameters = null;
    
    /**
     * constructor
     * @param sessionParams SessionParameters
     */
    public CommonAnalyser(final SessionParameters sessionParams) {
        
        this.sessionParameters = sessionParams;
        
    }
 
    
    
    
    /**
     * analyse the name of the operation,
     * return true if ok
     * @param opName String
     * @return boolean
     * @throws Exception e
     */
    public boolean analyseOperationName(final String opName) throws Exception {
        
       boolean opNameIsValid = false;
        
        
        if (opName.equals(SessionParameters.GETCAPABILITIES)) {
            
            opNameIsValid = true;
            sessionParameters.setOperationIsGetCap(true);
        
        } else if (opName.equals(SessionParameters.GETRECORDS)) {
            
            opNameIsValid = true;
            sessionParameters.setOperationIsGetRecs(true);
            
        } else if (opName.equals(SessionParameters.GETRECORDBYID)) {
        
            opNameIsValid = true;
            sessionParameters.setOperationIsGetRecById(true);
            
        } else if (opName.equals(SessionParameters.DESCRIBERECORD)) {
        
            opNameIsValid = true;
            sessionParameters.setOperationIsDescRec(true); 
        }
        
        return opNameIsValid;
    }
    
    
    
    /**
     * analyse the id(s)
     * return true if ok 
     * @param ids String
     * @return boolean
     */
    public boolean analyseIds(final String ids) {
        
        boolean idsIsValid = false; 
        
        StringTokenizer stringTokenizer = new StringTokenizer(ids, ", ");
        
        ArrayList idsList = new ArrayList();
        
        
        while (stringTokenizer.hasMoreTokens()) {
            
            idsList.add(stringTokenizer.nextToken());
            
        }
        
        
        if (idsList.size() > 0) {
            
            idsIsValid = true;
            sessionParameters.setIdsList(idsList);
            
        }
        
        // no matter if it is invalid
        sessionParameters.setIds(ids);
        
        return idsIsValid;
    }
    
    
    /**
     * analyse the type names,
     * return true if ok
     * @param typeNames String
     * @return boolean
     */
    public boolean analyseTypeNames(final String typeNames) {
          
          boolean typeNamesIsValid = false; 
          
          String typeNameCurrent = null;
          
          StringTokenizer stringTokenizer = new StringTokenizer(typeNames, ", ");
          

          while (stringTokenizer.hasMoreTokens()) {
            
              typeNameCurrent = stringTokenizer.nextToken();
          
              if (typeNameCurrent.equalsIgnoreCase("csw:dataset")) {
                  
                  typeNamesIsValid = true;
                  sessionParameters.setTypeNameIsDataset(true);
                 
                  
              } else if (typeNameCurrent.equalsIgnoreCase("csw:datasetcollection")) {
                  
                  typeNamesIsValid = true;
                  sessionParameters.setTypeNameIsDatasetcollection(true);
                 
                  
             } else if (typeNameCurrent.equalsIgnoreCase("csw:service")) {
              
                 typeNamesIsValid = true;
                 sessionParameters.setTypeNameIsService(true);
                 
             
             } else if (typeNameCurrent.equalsIgnoreCase("csw:application")) {
                 
                 typeNamesIsValid = true;
                 sessionParameters.setTypeNameIsApplication(true);
                
             }
              
          }
          
          
          //no matter if it is invalid
          sessionParameters.setTypeNames(typeNames); 
         
          
          return typeNamesIsValid;
      }  
    
    
  /**
   * analyse the result type,
   * return true if ok
   * @param be SOAPBodyElement
   * @return boolean
   * @throws Exception e
   */
public boolean analyseResultType(final SOAPBodyElement be) throws Exception {
        
        
        String resultType = null;
        
        
        
        resultType = be.getAttribute("resultType");
        
        
        if (resultType != null) {
            
            
            //TODO validate? 
           //Validate the request and return an Acknowledgement message if it 
      	   //passes. Continue processing the request asynchronously.
      	  
            if (resultType.equalsIgnoreCase("HITS") ||
               resultType.equalsIgnoreCase("RESULTS") 
               // || resultType.equalsIgnoreCase("VALIDATE")
               ) {
                
                sessionParameters.setResultType(resultType);
                
            } else {
                
                Exception e = 
                    new CSWInvalidParameterValueException("Attribute 'resultType' is invalid.", "resultType"); 
                
                throw e;     
                
            }
            
        
        }
        
        
        
        return true;  
    }
    
    
    
 /**
  * analyse the element (set) name,
  * return true if ok
  * @param be SOAPBodyElement
  * @return boolean
  * @throws Exception e
  */
    public boolean analyseElementSetName(final SOAPBodyElement be) throws Exception {
        
        String elementSetName = null;
        
        Element elemElementSetName = null;
        
        NodeList nl = be.getElementsByTagName("ElementSetName");
        
        if (nl == null || nl.getLength() == 0) {
            
            
            nl = be.getElementsByTagName("ElementName");
            
        }
        
        
        if (nl != null && nl.getLength() != 0) {
            
            elemElementSetName = (Element) nl.item(0);
            
        }    
            
            
        if (elemElementSetName != null) {
            
           
            if (elemElementSetName.getNodeName().equals("ElementSetName") || 
                 elemElementSetName.getNodeName().equals("ElementName")) {
                
                elementSetName = elemElementSetName.getFirstChild().getNodeValue();
               
                if (elementSetName != null) {
                    
                     
                     if (elementSetName.equalsIgnoreCase("full") ||
                        elementSetName.equalsIgnoreCase("brief") ||
                        elementSetName.equalsIgnoreCase("summary")) {
                         
                         sessionParameters.setElementSetName(elementSetName);
                         
                     } else {
                         
                         Exception e = 
                             new CSWInvalidParameterValueException("Value of 'ElementSetName' is invalid.", 
                                                                   "ElementSetName"); 
                         
                         throw e;   
                         
                     }
                     
                }
            }
            
        }
        
        
        
        return true; 
    }
    
    
    /**
    * analyse the output schema,
    * return true if ok
    * @param be SOAPBodyElement
    * @return boolean
    * @throws Exception e
    */
    public  boolean analyseOutputSchema(final SOAPBodyElement be) throws Exception {
        
        String outputSchema = null;
        
        outputSchema = be.getAttribute("outputSchema");
        
        
        if (outputSchema != null) {
            
           
            if (outputSchema.equalsIgnoreCase("CSW:OGCCORE") ||  
                outputSchema.equalsIgnoreCase("CSW:PROFILE")) {
                
                sessionParameters.setOutputSchema(outputSchema);
                
            } else {
                
                Exception e = 
                    new CSWInvalidParameterValueException("Attribute 'outputSchema' is invalid.", "outputSchema"); 
                
                throw e;     
                
            }
                
        }
        
        
        return true; 
    }
    
    
    /**
     * analyse the output format,
     * return true if ok
     * @param be SOAPBodyElement
     * @return boolean
     * @throws Exception e
     */
    public boolean analyseOutputFormat(final SOAPBodyElement be) throws Exception {
        
        String outputFormat = null;
        
        outputFormat = be.getAttribute("outputFormat");
        
        
        if (outputFormat != null && !outputFormat.equalsIgnoreCase("text/xml")) {
            
            Exception e = 
                new CSWInvalidParameterValueException("Attribute 'outputFormat' is not 'text/xml'.", "outputFormat"); 
            
            throw e;     
            
        }
        
        return true;
    }
   
    
    /**
     * analyse the schema language,
     * return true if ok
     * @param be SOAPBodyElement
     * @return boolean
     * @throws Exception e
     */
    public boolean analyseSchemaLanguage(final SOAPBodyElement be) throws Exception {
        
        String schemaLanguage = null;
        
        schemaLanguage = be.getAttribute("schemaLanguage");
        
        
        if (schemaLanguage != null && !schemaLanguage.equalsIgnoreCase("XMLSCHEMA")) {
            
            Exception e = 
                new CSWInvalidParameterValueException("Attribute 'schemaLanguage' is not 'XMLSCHEMA'.", 
                                                      "schemaLanguage"); 
            
            throw e;     
            
        }
        
        return true;
    }
    
    
    /**
     * analyse the service,
     * return true if ok
     * @param be SOAPBodyElement
     * @return boolean
     * @throws Exception e
     */
    public  boolean analyseService(final SOAPBodyElement be) throws Exception {
        
        String service = null;
        
        service = be.getAttribute("service");
        
        if (service == null) {
            
           
            Exception e = 
                new CSWMissingParameterValueException("Attribute 'service' is missing.", "service"); 
            
            throw e;     
        
        }
        
        if (!service.equals("CSW")) {
          
            Exception e = 
                new CSWInvalidParameterValueException("Attribute 'service' is not 'CSW'.", "service"); 
            
            throw e;     
            
        }
        
        
        return true;
    }
    
    
    
    /**
     * analyse the version,
     * return true if ok
     * @param be SOAPBodyElement
     * @return boolean
     * @throws Exception e
     */    
    public boolean analyseVersion(final SOAPBodyElement be) throws Exception {
    
      
      String version = null;
     
      
      version = be.getAttribute("version");
      
     
      if (version == null) {
          
         
          Exception e = 
              new CSWMissingParameterValueException("Attribute 'version' is missing.", "version"); 
          
          throw e;     
      
      }
      
     
      if (!version.equals("2.0.0")) {
        
          Exception e = 
              new CSWInvalidParameterValueException("Attribute 'version' is not '2.0.0'.", "version"); 
          
          throw e;     
          
      }
        
        
      return true;
    }   

}