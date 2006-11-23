/*
 * Copyright (c) 1997-2006 by media style GmbH
 */
package de.ingrid.ibus.processor;

import de.ingrid.utils.processor.IPreProcessor;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;

/**
 * Translates the portal metaclass query syntax into udk indexed fieldnames.
 * 
 * <p/>created on 19.05.2006
 * 
 * @version $Revision: $
 * @author jz
 * @author $Author: ${lastedit}
 * 
 */
public class UdkMetaclassPreProcessor implements IPreProcessor {

    /**
     * Name of the field that contains the metclass.
     */
    public static final String PORTAL_METACLASS = "metaclass";

    /**
     * Substitution for metaclass field name.
     */
    public static final String UDK_METACLASS = "t01_object.obj_class";

    /**
     * Name of the metaclass database value that should be substituted.
     */
    public static final String PORTAL_METACLASS_DATABASE = "database";

    /**
     * Substitution for metaclass database value.
     */
    public static final String UDK_METACLASS_DATABASE = "5";

    /**
     * Name of the metaclass service value that should be substituted.
     */
    public static final String PORTAL_METACLASS_SERVICE = "service";

    /**
     * Substitution for metaclass service value.
     */
    public static final String UDK_METACLASS_SERVICE = "3";

    /**
     * Name of the metaclass document value that should be substituted.
     */
    public static final String PORTAL_METACLASS_DOCUMENT = "document";

    /**
     * Substitution for metaclass document value.
     */
    public static final String UDK_METACLASS_DOCUMENT = "2";

    /**
     * Name of the metaclass map value that should be substituted.
     */
    public static final String PORTAL_METACLASS_MAP = "map";

    /**
     * Substitution for metaclass map value.
     */
    public static final String UDK_METACLASS_MAP = "1";

    /**
     * Name of the metaclass job value that should be substituted.
     */
    public static final String PORTAL_METACLASS_JOB = "job";

    /**
     * Substitution for metaclass job value.
     */
    public static final String UDK_METACLASS_JOB = "0";

    /**
     * Name of the metaclass project value that should be substituted.
     */
    public static final String PORTAL_METACLASS_PROJECT = "project";

    /**
     * Substitution for metaclass project value.
     */
    public static final String UDK_METACLASS_PROJECT = "4";

    public void process(IngridQuery query) throws Exception {
        IngridQuery[] clauses = query.getAllClauses();
        for (int i = 0; i < clauses.length; i++) {
            FieldQuery oldField = clauses[i].removeField(PORTAL_METACLASS);
            while (oldField != null) {
                clauses[i].addField(new FieldQuery(oldField.isRequred(), oldField.isProhibited(), UDK_METACLASS,
                        getDbValue(oldField.getFieldValue())));
                oldField = clauses[i].removeField(PORTAL_METACLASS);
            }
        }
    }

    private String getDbValue(String fieldValue) {
        if (fieldValue.equalsIgnoreCase(PORTAL_METACLASS_DATABASE)) {
            return UDK_METACLASS_DATABASE;
        } else if (fieldValue.equalsIgnoreCase(PORTAL_METACLASS_SERVICE)) {
            return UDK_METACLASS_SERVICE;
        } else if (fieldValue.equalsIgnoreCase(PORTAL_METACLASS_DOCUMENT)) {
            return UDK_METACLASS_DOCUMENT;
        } else if (fieldValue.equalsIgnoreCase(PORTAL_METACLASS_MAP)) {
            return UDK_METACLASS_MAP;
        } else if (fieldValue.equalsIgnoreCase(PORTAL_METACLASS_JOB)) {
            return UDK_METACLASS_JOB;
        } else if (fieldValue.equalsIgnoreCase(PORTAL_METACLASS_PROJECT)) {
            return UDK_METACLASS_PROJECT;
        } else {
            throw new IllegalArgumentException("unknown metaclass '" + fieldValue + '\'');
        }
    }
}
