/*
 * Created on 29.09.2005
 *
 */
package de.ingrid.ibus.cswinterface.analyse;

import javax.xml.soap.SOAPBodyElement;

/**
 * @author rschaefer
 *
 */
public interface CSWAnalyser {
    
    
     /**
      * 
     * @param be SOAPBodyElement
     * @return boolean 
     * @throws Exception e
     */
    boolean analyse(final SOAPBodyElement be) throws Exception;
    
    
    /**
     * 
    * @param kvpStr String
    * @return boolean 
    * @throws Exception e
    */
   boolean analyse(final String kvpStr) throws Exception;
    

}