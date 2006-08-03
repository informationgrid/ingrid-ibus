/*
 * Copyright (c) 1997-2006 by media style GmbH
 */

package de.ingrid.ibus.processor;

import de.ingrid.utils.processor.IPreProcessor;
import de.ingrid.utils.query.ClauseQuery;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;

/**
 * Substitute special field names like zip, city, street with udk specific index
 * names.
 * 
 * <p/>created on 01.06.2006
 * 
 * @version $Revision: $
 * @author jz
 * @author $Author: ${lastedit}
 * 
 */
public class AddressPreProcessor implements IPreProcessor {

    /***/
    public static final String ZIP = "zip";

    /***/
    // public static final String ZIP_UDK_NAME1 = "t02_address.postcode";

    /***/
    public static final String ZIP_UDK_NAME2 = "t02_address.postbox_pc";

//    /***/
//    public static final String CITY = "city";
//
//    /***/
//    public static final String CITY_UDK_NAME = "t02_address.city";
//
//    /***/
//    public static final String STREET = "street";
//
//    /***/
//    public static final String STREET_UDK_NAME = "t02_address.street";

    public void process(IngridQuery query) throws Exception {
        IngridQuery[] clauses = query.getAllClauses();
        for (int i = 0; i < clauses.length; i++) {
            checkZip(clauses[i]);
            // checkStreet(clauses[i]);
            //            checkCity(clauses[i]);
        }

    }

//    private void checkCity(IngridQuery query) {
//        FieldQuery oldField = query.removeField(CITY);
//        if (oldField != null) {
//            query.addField(new FieldQuery(oldField.isRequred(), oldField.isProhibited(), CITY_UDK_NAME, oldField
//                    .getFieldValue()));
//        }
//    }
//
//    private void checkStreet(IngridQuery query) {
//        FieldQuery oldField = query.removeField(STREET);
//        if (oldField != null) {
//            query.addField(new FieldQuery(oldField.isRequred(), oldField.isProhibited(), STREET_UDK_NAME, oldField
//                    .getFieldValue()));
//        }
//    }

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
