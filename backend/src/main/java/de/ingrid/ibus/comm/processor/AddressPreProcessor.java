/*
 * **************************************************-
 * InGrid iBus
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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

/**
 * Substitute special field names like zip, city, street with udk specific index
 * names. Only the zip is substituted.
 * 
 * <p/>created on 01.06.2006
 * 
 * @version $Revision: $
 * @author jz
 * @author $Author: ${lastedit}
 * 
 */
public class AddressPreProcessor implements IPreProcessor {

    /**
     * String to substitute with real udk zip name.
     */
    public static final String ZIP = "zip";

    /**
     * Substitution for query zip field.
     */
    public static final String ZIP_UDK_NAME2 = "t02_address.postbox_pc";

    public void process(IngridQuery query) throws Exception {
        IngridQuery[] clauses = query.getAllClauses();
        for (int i = 0; i < clauses.length; i++) {
            checkZip(clauses[i]);
        }

    }

    private void checkZip(IngridQuery query) {
        FieldQuery oldField = query.removeField(ZIP);
        if (oldField != null) {
            ClauseQuery clauseQuery = new ClauseQuery(oldField.isRequred(), oldField.isProhibited());
            clauseQuery.addField(new FieldQuery(false, false, ZIP, oldField.getFieldValue()));
            clauseQuery.addField(new FieldQuery(false, false, ZIP_UDK_NAME2, oldField.getFieldValue()));
            query.addClause(clauseQuery);
        }
    }

}
