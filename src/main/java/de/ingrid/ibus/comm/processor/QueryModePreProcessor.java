/*
 * **************************************************-
 * InGrid iBus
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/*
 * Copyright (c) 1997-2006 by media style GmbH
 */

package de.ingrid.ibus.comm.processor;

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
