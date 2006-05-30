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
    
    /***/
    public static final String PORTAL_METACLASS = "metaclass";

    /***/
    public static final String UDK_METACLASS = "t01_object.obj_class";

    /***/
    public static final String PORTAL_METACLASS_DATABASE = "database";

    /***/
    public static final String UDK_METACLASS_DATABASE = "5";

    /***/
    public static final String PORTAL_METACLASS_SERVICE = "service";

    /***/
    public static final String UDK_METACLASS_SERVICE = "3";

    /***/
    public static final String PORTAL_METACLASS_DOCUMENT = "document";

    /***/
    public static final String UDK_METACLASS_DOCUMENT = "2";

    /***/
    public static final String PORTAL_METACLASS_MAP = "map";

    /***/
    public static final String UDK_METACLASS_MAP = "1";

    /***/
    public static final String PORTAL_METACLASS_JOB = "job";

    /***/
    public static final String UDK_METACLASS_JOB = "0";

    /***/
    public static final String PORTAL_METACLASS_PROJECT = "project";

    /***/
    public static final String UDK_METACLASS_PROJECT = "4";

    public void process(IngridQuery query) throws Exception {
        IngridQuery[] clauses=query.getAllClauses();
        for (int i = 0; i < clauses.length; i++) {
            FieldQuery oldField = clauses[i].removeField(PORTAL_METACLASS);
            if (oldField != null) {
                query.addField(new FieldQuery(oldField.isRequred(), oldField.isProhibited(), UDK_METACLASS,
                        getDbValue(oldField.getFieldValue())));
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
            throw new IllegalArgumentException("unknown metaclass '" + fieldValue + "'");
        }
    }
}
