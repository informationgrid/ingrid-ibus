/*
 * Created on 07.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.ibus.cswinterface.transform;

import java.io.StringReader;

import javax.xml.soap.SOAPElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.ingrid.ibus.cswinterface.tools.SOAPTools;
import de.ingrid.ibus.cswinterface.tools.XMLTools;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * @author rschaefer
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RequestTransformer implements CSWRequestTransformer {

    /**
     * 
     * @see de.ingrid.ibus.cswinterface.transform.CSWRequestTransformer#transform(javax.xml.soap.SOAPElement)
     */
    public final IngridQuery transform(final SOAPElement soapElementFilter) throws Exception {

        IngridQuery ingridQuery = null;
        
        String luceneQuery = null;
        
        FilterImpl filter = getFilterFromSOAPElem(soapElementFilter);
        
        /* 
         FilterToIngridQuery filterToIngridQuery = new FilterToIngridQuery();
        
         ingridQuery = filterToIngridQuery.generateQueryFromFilter(filter);   
        */
       
        FilterToLuceneQuery filterToLuceneQuery = new FilterToLuceneQuery();
       
        luceneQuery = filterToLuceneQuery.generateQueryFromFilter(filter);
       
        //System.out.println("RequestTransformer luceneQuery: " + luceneQuery);
        
        QueryStringParser parser = new QueryStringParser(new StringReader(luceneQuery));
        
        ingridQuery = parser.parse();
        
        
        return ingridQuery;
    }

    
    /**
     * 
     * @param soapElementFilter SOAPElement
     * @return FilterImpl
     * @throws Exception e
     */
    public final FilterImpl getFilterFromSOAPElem(final SOAPElement soapElementFilter) throws Exception {
        
        Element  elemFilter = null;
        
        Document doc = XMLTools.create();
	    
        doc.appendChild(doc.createElement("Filter"));
	    
        elemFilter = doc.getDocumentElement();
		
        elemFilter = (Element) SOAPTools.copyNode(soapElementFilter, elemFilter);
       
        return new FilterImpl(elemFilter);
       
    }
    
}
