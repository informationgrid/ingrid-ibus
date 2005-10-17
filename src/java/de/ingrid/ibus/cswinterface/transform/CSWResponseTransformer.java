/*
 * Created on 07.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.ibus.cswinterface.transform;

import org.w3c.dom.Document;

import de.ingrid.utils.IngridDocument;



/**
 * @author rschaefer
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface CSWResponseTransformer {
    
   
    /**
     * This Method transforms an array of IngridDocuments into 
     * a DOM Document (OGC XML Metadata Response).
     * @param ingridDocuments IngridDocument[]
     * @return document Document
     * @throws Exception e
     */
    Document transform(final IngridDocument[] ingridDocuments) throws Exception;

}
