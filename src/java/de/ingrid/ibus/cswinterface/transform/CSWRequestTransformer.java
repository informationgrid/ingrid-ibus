/*
 * Created on 07.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.ibus.cswinterface.transform;


import javax.xml.soap.SOAPElement;

import de.ingrid.utils.query.IngridQuery;

/**
 * @author rschaefer
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface CSWRequestTransformer {
    
    /**
     * This Method transforms an OGC XML Filter into 
     * an IngridQuery.
     * @param soapElementFilter SOAPElement
     * @return ingridQuery IngridQuery
     * @throws Exception e
     */
    IngridQuery transform(final SOAPElement soapElementFilter) throws Exception;

}
