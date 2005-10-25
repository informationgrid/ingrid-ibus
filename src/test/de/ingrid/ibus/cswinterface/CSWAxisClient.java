/*
 * Created on 05.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.ibus.cswinterface;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.client.Call;
import org.apache.axis.soap.SOAPConstants;

import de.ingrid.ibus.cswinterface.tools.AxisTools;

/**
 * @author rschaefer
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CSWAxisClient {
    
    

    
    
    public static void main(final String[] args) {
        
       Message smsg = null;
       
       Message respsmsg = null; 
        
      Call call = null;
      
      
      try {
      
           smsg = AxisTools.createSOAPMessage(TestRequests.GETCAP1);
          
          //smsg = AxisTools.createSOAPMessage(TestRequests.GETCAPINVALID1);
          
          //smsg = AxisTools.createSOAPMessage(TestRequests.GETREC1);
   
      } catch (Exception e2) {
        // TODO Auto-generated catch block
        e2.printStackTrace();
    }
      
      
    try {
        
         call = new Call("http://localhost:8080/csw/csw");
        
        //call = new Call("https://localhost:80/csw/csw");
        
        //call = new Call("http://www.uok.bayern.de/axis/services/UDK_Soap_Service");
         
        //call = new Call("http://146.140.211.20:8080/wcas/ingeowcas");
        
        //call = new Call("http://localhost:8080/advwcasportal/advwcasportal");
        
        
        
        
    } catch (MalformedURLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    
     //set SOAP Version 
      
       call.setSOAPVersion(SOAPConstants.SOAP12_CONSTANTS);
     
       //call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
     
      call.setRequestMessage(smsg);
      
   
      try {
          
        call.invoke();
        
        
        
       respsmsg = call.getResponseMessage();
        
       
       
       try {
           
        if (!AxisTools.isSOAP12((Message) respsmsg)) {
               
            System.out.println("CSW Axis Client: received message is SOAP 1.1.");
        
        } else {
            
            System.out.println("CSW Axis Client: received message is SOAP 1.2.");
        }
        
        
        
    } catch (Exception e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
    }
       
     
       
       ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		  
       respsmsg.writeTo(byteArrayOutputStream);
	 	  
	   System.out.println("CSW Axis Client: received message: " + byteArrayOutputStream.toString());
       
        
    } catch (AxisFault e) {
        
        // TODO Auto-generated catch block
        e.printStackTrace();
        
          
    
    } catch (SOAPException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
        
        
    }
    
    
    
    

}
