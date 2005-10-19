/*
 * Created on 18.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.ibus.cswinterface;

import javax.xml.soap.SOAPBodyElement;

import org.apache.axis.Message;
import org.apache.axis.message.SOAPBody;

import de.ingrid.ibus.cswinterface.analyse.CommonAnalyser;
import de.ingrid.ibus.cswinterface.analyse.DescRecAnalyser;
import de.ingrid.ibus.cswinterface.analyse.GetCapAnalyser;
import de.ingrid.ibus.cswinterface.analyse.GetRecAnalyser;
import de.ingrid.ibus.cswinterface.analyse.GetRecByIdAnalyser;
import de.ingrid.ibus.cswinterface.analyse.SessionParameters;
import de.ingrid.ibus.cswinterface.exceptions.CSWOperationNotSupportedException;

/**
 * @author rschaefer
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CSW {
    
    
    
    /**
     * 
     * @param soapRequestMessage Message
     * @return soapResponseMessage Message
     */
    public final Message performMessage(final Message soapRequestMessage) throws Exception {
    
        Message soapResponseMessage = null;
        
        SessionParameters sessionParameters = new SessionParameters();
        
        CommonAnalyser commonAnalyser = new CommonAnalyser(sessionParameters);
        
        
        SOAPBody body = (SOAPBody) soapRequestMessage.getSOAPBody();
        
        SOAPBodyElement be = (SOAPBodyElement) body.getFirstChild();
        
       
        String opName = be.getNodeName();
        
        //TODO remove opName analyse in analysers?
        if (!commonAnalyser.analyseOperationName(opName)) {
            
            Exception e = 
                new CSWOperationNotSupportedException("Operation '" + opName +
                                       "' is not supported.", opName); 
              
              throw e; 
            
            
        } 
        
        
        if (sessionParameters.isOperationIsGetCap()) {
            
            
            GetCapAnalyser getCapAnalyser = new GetCapAnalyser();
            
            
            if (getCapAnalyser.analyse(be)) {
                
                //FIXME only for testing
     		   
                soapResponseMessage = soapRequestMessage;
                //TODO
            }
            
          
            
            
            
        } else if (sessionParameters.isOperationIsGetRecs()) {
            
            GetRecAnalyser getRecAnalyser = new GetRecAnalyser(sessionParameters);
            
            if (getRecAnalyser.analyse(be)) {
                
                //TODO
                
            }
            
            
        } else if (sessionParameters.isOperationIsGetRecById()) {
            
            GetRecByIdAnalyser getRecByIdAnalyser = new GetRecByIdAnalyser(sessionParameters);
            
            if (getRecByIdAnalyser.analyse(be)) {
                
                //TODO
            }
            
            
        } else if (sessionParameters.isOperationIsDescRec()) {
            
            DescRecAnalyser descRecAnalyser = new DescRecAnalyser();
            
            if (descRecAnalyser.analyse(be)) {
                
                //TODO
            }
            
        }
        
        
        
        
        return soapResponseMessage;
    }
    
    

}
