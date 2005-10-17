/*
 * Created on 10.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.ibus.cswinterface.transform;

import de.ingrid.utils.query.IngridQuery;

/**
 * @author rschaefer
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class IngridQueryToLuceneQuery {
    
    
    public final String transform(final IngridQuery ingridQuery) {
        
        String luceneQuery = null;
        
        
        StringBuffer buffer = new StringBuffer();
        buffer.append("(");
        //buffer.append(" terms: ");
        appendToString(buffer, ingridQuery.getTerms());
        //buffer.append(" fields: ");
        appendToString(buffer, ingridQuery.getFields());
        //buffer.append(" clauses: ");
        IngridQuery[] clauses = ingridQuery.getClauses();
        for (int i = 0; i < clauses.length; i++) {
           
            buffer.append(getOpString(clauses[i].getOperation()) + " " );
            
            buffer.append(clauses[i].getDescription());

        }
        buffer.append(")");
        
        luceneQuery = buffer.toString();
        
        
       
        return  luceneQuery;
    }
    
    
    /**
     * @param buffer
     * @param terms
     */
    private void appendToString(StringBuffer buffer, IngridQuery[] terms) {
        
        for (int i = 0; i < terms.length; i++) {
            
           String strOp = getOpString(terms[i].getOperation());
           
           buffer.append(" " + strOp + " " );
            
            
            buffer.append(terms[i]);
            buffer.append(" ");
        }
    }

    
    private String getOpString(int operation) {
        
        String strOp = null;
        
        switch (operation) {
        
         case -1: strOp = "NOT";
                 break;
         case 0: strOp = "AND";
                 break;
         case 1: strOp = "OR";
                 break;
         default : strOp = "AND";
        
        } 
        
        return strOp;
    }
    

}
