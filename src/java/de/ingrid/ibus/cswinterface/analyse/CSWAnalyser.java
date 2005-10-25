/*
 * Created on 29.09.2005
 *
 */
package de.ingrid.ibus.cswinterface.analyse;

import javax.xml.soap.SOAPBodyElement;

/**
 * interface for OGC CSW analysers
 * @author rschaefer
 */
public interface CSWAnalyser {
    
    
     /**
      * analyse the SOAP message
      * and collect some values.
      * return true if ok.
     * @param be SOAPBodyElement
     * @return boolean 
     * @throws Exception e
     */
    boolean analyse(final SOAPBodyElement be) throws Exception;
    
    
    /**
     * analyse the keyword value pair (KVP) string 
     * and collect some values.
      * return true if ok.
    * @param kvpStr String
    * @return boolean 
    * @throws Exception e
    */
   boolean analyse(final String kvpStr) throws Exception;
    

}