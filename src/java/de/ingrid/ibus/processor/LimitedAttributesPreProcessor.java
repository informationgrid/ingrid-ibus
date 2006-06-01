/*
 * Copyright (c) 1997-2006 by media style GmbH
 */

package de.ingrid.ibus.processor;

import de.ingrid.utils.processor.IPreProcessor;
import de.ingrid.utils.query.ClauseQuery;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.query.TermQuery;

/**
 * If a query contains the field 'attribrange:limited' all terms get substitute
 * with predefined field queries.
 * 
 * <p/>created on 31.05.2006
 * 
 * @version $Revision: $
 * @author jz
 * @author $Author: ${lastedit}
 * 
 */
public class LimitedAttributesPreProcessor implements IPreProcessor {

    /***/
    public static final String ATTRIBUTE_RANGE = "attribrange";

    /***/
    public static final String ATTRIBUTE_RANGE_LIMITED = "limited";

    /***/
    public static final String INSTITUTION = "T02_address.institution";

    /***/
    public static final String PERSON_LAST_NAME = "T02_address.lastname";
    
    /***/
    public static final String PERSON_FIRST_NAME = "T02_address.lastname";

    /***/
    public static final String DESCRIPTION = "T02_address.descr";

    /***/
    public static final String SEARCHTERMS = "T04_search.searchterm";

    public void process(IngridQuery query) throws Exception {
        IngridQuery[] clauses = query.getAllClauses();
        FieldQuery limitedField = query.removeField(ATTRIBUTE_RANGE);
        if (limitedField == null) {
            return;
        }
        for (int i = 0; i < clauses.length; i++) {
            TermQuery[] terms = clauses[i].getTerms();
            for (int j = 0; j < terms.length; j++) {
                addLimitedFieldsClause(clauses[i], terms[j]);
            }
            clauses[i].remove(IngridQuery.TERM_KEY);
        }
    }

    private void addLimitedFieldsClause(IngridQuery query, TermQuery term) {
        ClauseQuery limitedClause = new ClauseQuery(term.isRequred(), term.isProhibited());
        limitedClause.addField(new FieldQuery(false, false, INSTITUTION, term.getTerm()));
        limitedClause.addField(new FieldQuery(false, false, PERSON_FIRST_NAME, term.getTerm()));
        limitedClause.addField(new FieldQuery(false, false, PERSON_LAST_NAME, term.getTerm()));
        limitedClause.addField(new FieldQuery(false, false, DESCRIPTION, term.getTerm()));
        limitedClause.addField(new FieldQuery(false, false, SEARCHTERMS, term.getTerm()));
        query.addClause(limitedClause);
    }

}
