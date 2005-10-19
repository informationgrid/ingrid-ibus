/*
 * Created on 18.10.2005
 *
 */
package de.ingrid.ibus.cswinterface.tools;

import java.io.ByteArrayOutputStream;

import javax.xml.soap.SOAPException;

import org.apache.axis.Message;

import junit.framework.TestCase;

/**
 * @author rschaefer
 *
 */
public class SOAPToolsTest extends TestCase {

    public final void testCreateExceptionReport() throws Exception {
        
        Message exceptionMess = null;
        
        exceptionMess = SOAPTools.createExceptionReport("exception text", "exception code", "locator");
        
        assertNotNull(exceptionMess);
        
   
        //System.out.println("exceptionMess.getSOAPPartAsString(): " + exceptionMess.getSOAPPartAsString());
        
    }

}
