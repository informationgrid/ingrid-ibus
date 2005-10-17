/*
 * Created on 29.09.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.ibus.cswinterface.analyse;

import javax.xml.soap.SOAPBodyElement;

/**
 * @author rschaefer
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
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