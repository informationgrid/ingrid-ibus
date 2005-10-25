/*
 * Created on 18.10.2005
 *
 */
package de.ingrid.ibus.cswinterface;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;

import org.apache.axis.Message;
import org.apache.axis.SOAPPart;
import org.apache.axis.message.SOAPBody;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.soap.SOAPConstants;
import org.w3c.dom.Document;

import de.ingrid.ibus.Bus;
//import de.ingrid.ibus.DummyProxyFactory;
import de.ingrid.ibus.cswinterface.analyse.CommonAnalyser;
import de.ingrid.ibus.cswinterface.analyse.DescRecAnalyser;
import de.ingrid.ibus.cswinterface.analyse.GetCapAnalyser;
import de.ingrid.ibus.cswinterface.analyse.GetRecAnalyser;
import de.ingrid.ibus.cswinterface.analyse.GetRecByIdAnalyser;
import de.ingrid.ibus.cswinterface.analyse.SessionParameters;
import de.ingrid.ibus.cswinterface.exceptions.CSWOperationNotSupportedException;
import de.ingrid.ibus.cswinterface.tools.SOAPTools;
import de.ingrid.ibus.cswinterface.tools.XMLTools;
import de.ingrid.ibus.cswinterface.tools.AxisTools;
import de.ingrid.ibus.cswinterface.transform.RequestTransformer;
import de.ingrid.iplug.PlugDescription;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * This class represents the catalogue service web (CSW)
 *
 * @author rschaefer
 */
public class CSW {
    
    
    
    /**
     * performs a SOAP request
     * @param soapRequestMessage Message
     * @return soapResponseMessage Message
     * @throws Exception e
     */
    protected final Message doRequest(final Message soapRequestMessage) throws Exception {
    
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
                
                soapResponseMessage = doGetCapabilitiesRequest();
                //FIXME only for testing
     		   //soapResponseMessage = soapRequestMessage;
               
            }
            
          
            
            
            
        } else if (sessionParameters.isOperationIsGetRecs()) {
            
            GetRecAnalyser getRecAnalyser = new GetRecAnalyser(sessionParameters);
            
            if (getRecAnalyser.analyse(be)) {
                
                //TODO do request
                
                soapResponseMessage = doGetRecordsRequest(be, sessionParameters);
                
                //FIXME only for testing
      		    soapResponseMessage = soapRequestMessage;
                
            }
            
            
        } else if (sessionParameters.isOperationIsGetRecById()) {
            
            GetRecByIdAnalyser getRecByIdAnalyser = new GetRecByIdAnalyser(sessionParameters);
            
            if (getRecByIdAnalyser.analyse(be)) {
                
                soapResponseMessage = doGetRecordByIdRequest(be, sessionParameters);
                
            }
            
            
        } else if (sessionParameters.isOperationIsDescRec()) {
            
            DescRecAnalyser descRecAnalyser = new DescRecAnalyser();
            
            if (descRecAnalyser.analyse(be)) {
                
                soapResponseMessage = doDescribeRecordRequest();
            }
            
        }
        
        
        
        
        return soapResponseMessage;
    }
    
    
    
    
    /**
     * performs a describe record request
     * @return soapResponseMessage Message
     * @throws Exception e
     */
    private Message doDescribeRecordRequest() throws Exception {
        
        Message soapResponseMessage = null;
        
        //FIXME just for testing: path should only be set by init.
        
        //TODO put ingrid schema into desc rec file
        //URL url = new URL(InitParameters.getDescribeRecordFile());
        
        URL url = new URL("file:///C:/Program Files/eclipse/workspace/ingrid-ibus/xml/csw_describe_record.xml");
        
        Reader reader = new InputStreamReader(url.openStream());
        
        Document doc = XMLTools.parse(reader);
        
        //works only with crimson !?!
        //System.out.println("cap doc: " + doc.getDocumentElement().toString());
        
        soapResponseMessage = AxisTools.createSOAPMessage(doc);
        
        return soapResponseMessage;
        
    }
    
    
    
    /**
     * performs a get capabilities request
     * @return soapResponseMessage Message
     * @throws Exception e
     */
    private Message doGetCapabilitiesRequest() throws Exception {
        
        Message soapResponseMessage = null;
        
        //FIXME just for testing: path should only be set by init.
        
        //URL url = new URL(InitParameters.getCapabilitiesFile());
        
        URL url = new URL("file:///C:/Program Files/eclipse/workspace/ingrid-ibus/xml/csw_capabilities.xml");
        
        Reader reader = new InputStreamReader(url.openStream());
        
        Document doc = XMLTools.parse(reader);
        
        //works only with crimson !?!
        //System.out.println("cap doc: " + doc.getDocumentElement().toString());
        
        soapResponseMessage = AxisTools.createSOAPMessage(doc);
        
        return soapResponseMessage;
    }
    
    
    
    /**
     * performs a get records request
     * @param be SOAPBodyElement
     * @param sessionParameters SessionParameters
     * @return soapResponseMessage Message
     * @throws Exception e
     */
    private Message doGetRecordsRequest(final SOAPBodyElement be, final SessionParameters sessionParameters) 
           throws Exception {
        
        Message soapResponseMessage = null;
        
        //TODO store in session?
        IngridQuery ingridQuery = null;
        
        //TODO transform the OGC filter (request) into IngridQuery; including SessionParameters
        
        RequestTransformer requestTransformer = new RequestTransformer();
        
        SOAPElement soapElementFilter = null;
        
        soapElementFilter = (SOAPElement) be.getElementsByTagName("Filter").item(0);
        
        
        ingridQuery = requestTransformer.transform(soapElementFilter);
        
        //FIXME call search method of running ibus ..
        
        
        return soapResponseMessage;
    }
    
    
    /**
     * performs a get record by id request
     * @param be SOAPBodyElement
     * @param sessionParameters SessionParameters
     * @return soapResponseMessage Message
     * @throws Exception e
     */
    private Message doGetRecordByIdRequest(final SOAPBodyElement be, final SessionParameters sessionParameters) 
                     throws Exception {
        
        Message soapResponseMessage = null;
        
        //TODO store in session?
        IngridQuery ingridQuery = null;
        
        //put the ids of SessionParameters into IngridQuery
        RequestTransformer requestTransformer = new RequestTransformer();
        
        ingridQuery = requestTransformer.transform(sessionParameters.getIdsList());
        
        //FIXME call search method of running ibus ..
        
//        Bus bus = new Bus(new DummyProxyFactory());
//
//       
//        for (int i = 0; i < 3; i++) {
//            PlugDescription plug = new PlugDescription();
//            plug.setPlugId("" + i);
//            bus.getIPlugRegestry().addIPlug(plug);
//        }
//        
//        
//        IngridDocument[] documents = bus.search(ingridQuery, 10, 1, Integer.MAX_VALUE, 1000);
        
        
        
        return soapResponseMessage;
    }
    

}
