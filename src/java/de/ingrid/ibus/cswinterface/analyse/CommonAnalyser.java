/*
 * Created on 12.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.ibus.cswinterface.analyse;

import javax.xml.soap.SOAPBodyElement;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.ingrid.ibus.cswinterface.exceptions.CSWInvalidParameterValueException;
import de.ingrid.ibus.cswinterface.exceptions.CSWMissingParameterValueException;

/**
 * @author rschaefer
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class CommonAnalyser {
    
    private SessionParameters sessionParameters = null;
    
    public CommonAnalyser(final SessionParameters sessionParams) {
        
        this.sessionParameters = sessionParams;
        
    }
    
    
  public  boolean analyseResultType(final SOAPBodyElement be) throws Exception {
        
        
        String resultType = null;
        
        
        
        resultType = be.getAttribute("resultType");
        
        
        if (resultType != null) {
            
            
            //TODO validate? 
           //Validate the request and return an Acknowledgement message if it 
      	   //passes. Continue processing the request asynchronously.
      	  
            if (resultType.equalsIgnoreCase("HITS") ||
               resultType.equalsIgnoreCase("RESULTS") ||  
               resultType.equalsIgnoreCase("VALIDATE")) {
                
                sessionParameters.setResultType(resultType);
                
            } else {
                
                Exception e = 
                    new CSWInvalidParameterValueException("Attribute 'resultType' is invalid.", "resultType"); 
                
                throw e;     
                
            }
            
        
        }
        
        
        
        return true;  
    }
    
    
    
    public  boolean analyseElementSetName(final SOAPBodyElement be) throws Exception {
        
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
