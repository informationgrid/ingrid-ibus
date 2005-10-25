/*
 * Created on 10.10.2005
 *
 */
package de.ingrid.ibus.cswinterface.transform;

import de.ingrid.utils.query.IngridQuery;

/**
 * transforms an IngridQuery into a query string
 * @author rschaefer
 */
public class IngridQueryToString {
    
    
    /**
     * does the transforming
     * @param ingridQuery IngridQuery
     * @return queryString String
     */
    public final String transform(final IngridQuery ingridQuery) {
        
        String queryString = null;
        
        
        StringBuffer buffer = new StringBuffer();
        buffer.append("(");
        //buffer.append(" terms: ");
        appendToString(buffer, ingridQuery.getTerms());
        //buffer.append(" fields: ");
        appendToString(buffer, ingridQuery.getFields());
        //buffer.append(" clauses: ");
        IngridQuery[] clauses = ingridQuery.getClauses();
        for (int i = 0; i < clauses.length; i++) {
           
            buffer.append(getOpString(clauses[i].getOperation()) + " ");
            
           // buffer.append(clauses[i].getDescription());
            buffer.append(transform(clauses[i]));

        }
        buffer.append(")");
        
        queryString = buffer.toString();
        
        
       
        return  queryString;
    }
    
    
    /**
     * @param buffer StringBuffer
     * @param terms IngridQuery[]
     */
    private void appendToString(final StringBuffer buffer, final IngridQuery[] terms) {
        
        for (int i = 0; i < terms.length; i++) {
            
           String strOp = getOpString(terms[i].getOperation());
           
           buffer.append(" " + strOp + " ");
            
            
            buffer.append(terms[i]);
            buffer.append(" ");
        }
    }

    
    /**
     * returns the operator as a string
     * @param operation int
     * @return strOp String
     */
    private String getOpString(final int operation) {
        
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
