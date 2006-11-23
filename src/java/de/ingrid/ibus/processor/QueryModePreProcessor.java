/*
 * Copyright (c) 1997-2006 by media style GmbH
 */

package de.ingrid.ibus.processor;

import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.processor.IPreProcessor;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.query.TermQuery;

/**
 * If a query contains the field 'querymode' all terms gets processed
 * accordingly.
 * 
 * <p/>created on 31.05.2006
 * 
 * @version $Revision: $
 * @author jz
 * @author $Author: ${lastedit}
 *  
 */
public class QueryModePreProcessor implements IPreProcessor{
    
    /**
     * Name for the field that contains the query mode.
     */
    public static final String QUERYMODE = "querymode";

    /**
     * The value for a query mode substring.
     */
    public static final String QUERYMODE_SUBSTRING = "substring";

    public void process(IngridQuery query) throws Exception {
        FieldQuery queryModeField = query.removeField(QUERYMODE);
        if (queryModeField == null) {
            return;
        }
        if(QUERYMODE_SUBSTRING.equals(queryModeField.getFieldValue())){
            processSubstring(query);
        }else {
            throw new IllegalArgumentException("unknown querymode type '"+queryModeField.getFieldValue()+'\'');
        }
    }

    private void processSubstring(IngridQuery query) {
        IngridQuery[] clauses = query.getAllClauses();
        for (int i = 0; i < clauses.length; i++) {
            TermQuery[] terms = clauses[i].getTerms();
            for (int j = 0; j < terms.length; j++) {
                //make term a prefix term
                terms[j].put(IngridDocument.DOCUMENT_CONTENT,terms[j].getTerm()+'*');
            }
        }
    }

}
