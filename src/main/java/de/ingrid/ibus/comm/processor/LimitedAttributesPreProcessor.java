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

    /**
     * Name for the field that contains the attribute range. 
     */
    public static final String ATTRIBUTE_RANGE = "attribrange";

    /**
     * Limited value for the attribute range field.
     */
    public static final String ATTRIBUTE_RANGE_LIMITED = "limited";

    /**
     * Substitution for address institution.
     */
    public static final String INSTITUTION = "t02_address.institution";

    /**
     * Substitution for address person last name.
     */
    public static final String PERSON_LAST_NAME = "t02_address.lastname";
    
    /**
     * Substitution for address person first name.
     */
    public static final String PERSON_FIRST_NAME = "t02_address.firstname";

    /**
     * Substitution for address description.
     */
    public static final String DESCRIPTION = "t02_address.descr";

    /**
     * Substitution for search terms.
     */
    public static final String SEARCHTERMS = "t04_search.searchterm";

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
