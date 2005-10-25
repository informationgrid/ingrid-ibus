/*
 * Created on 07.10.2005
 *
 */
package de.ingrid.ibus.cswinterface.transform;

import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.soap.SOAPElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.ingrid.ibus.cswinterface.tools.SOAPTools;
import de.ingrid.ibus.cswinterface.tools.XMLTools;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * This class transforms an OGC XML Filter or 
 * a list of ids into an IngridQuery.
 * @author rschaefer
 */
public class RequestTransformer implements CSWRequestTransformer {

    /**
     * 
     * @see de.ingrid.ibus.cswinterface.transform.CSWRequestTransformer#transform(javax.xml.soap.SOAPElement)
     */
    public final IngridQuery transform(final SOAPElement soapElementFilter) throws Exception {

        IngridQuery ingridQuery = null;
        
        String ingridQueryString = null;
        
        FilterImpl filter = getFilterFromSOAPElem(soapElementFilter);
        
        /* 
         FilterToIngridQuery filterToIngridQuery = new FilterToIngridQuery();
        
         ingridQuery = filterToIngridQuery.generateQueryFromFilter(filter);   
        */
       
        FilterToIngridQueryString filterToIngrid = new FilterToIngridQueryString();
       
        ingridQueryString = filterToIngrid.generateQueryFromFilter(filter);
       
        //System.out.println("RequestTransformer luceneQuery: " + luceneQuery);
        
        QueryStringParser parser = new QueryStringParser(new StringReader(ingridQueryString));
        
        ingridQuery = parser.parse();
        
        
        //TODO set source:map or service?
        
        return ingridQuery;
    }

    
    
    
    
    /** 
     * 
     * @see de.ingrid.ibus.cswinterface.transform.CSWRequestTransformer#transform(java.util.List)
     */
    public final IngridQuery transform(final ArrayList idsList) throws Exception {
 
        // TODO implement
        IngridQuery ingridQuery = null;
        
        
        String queryString = "";
        
        //TODO name of field?
        String idField = "ID:";
    
        
        int listSize = idsList.size();
        
        
        for (int i = 0; i < listSize; i++) {
            
            //queryString = queryString + " AND " + idField + (String) idsList.get(i);
            
            queryString = queryString + " " + idField + (String) idsList.get(i);
            
        }
        
        //System.out.println("queryString: " + queryString);
        
        
        QueryStringParser parser = new QueryStringParser(new StringReader(queryString));
        
        ingridQuery = parser.parse();
        
        //TODO set source:map or service?
    
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
