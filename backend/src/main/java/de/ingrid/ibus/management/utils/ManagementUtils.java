/*
 * **************************************************-
 * Ingrid Management iPlug
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
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
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.ibus.management.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import de.ingrid.codelists.model.CodeListEntry;
import de.ingrid.ibus.management.ManagementIPlug;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;

/**
 * Utils class for the management iplug.
 * 
 * @author joachim@wemove.com
 */
public class ManagementUtils {
    
    private static final Log log = LogFactory.getLog( ManagementUtils.class );

    /**
     * Get a fields content as String.
     * 
     * @param query
     *            The query to check;
     * @param fieldName
     *            The field name to look for.
     * @return The result as a string. null if not found or the field is not of
     *         the type String.
     */
    public static String getField(IngridQuery query, String fieldName) {
        FieldQuery[] fields = query.getFields();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getFieldName().equalsIgnoreCase(fieldName)) {
                Object obj = fields[i].getFieldValue();
                if (obj instanceof String) {
                    return (String) obj;
                }
                break;
            }
        }
        return null;
    }

    /**
     * Checks if the array of datatypes contains a management datatype.
     * 
     * @param dataTypes
     *            The datatypes to check.
     * @return True if the management datatype exists, false, if not.
     */
    public static boolean containsManagementDataType(FieldQuery[] dataTypes) {
        int count = dataTypes.length;
        for (int i = 0; i < count; i++) {
            FieldQuery query = dataTypes[i];
            if (query.getFieldValue().equals(ManagementIPlug.DATATYPE_MANAGEMENT) && !query.isProhibited()) {
                return true;
            }
        }
        return false;
    }

    /**
     * get the language of the query i set.
     * 
     * @param query
     *            The query.
     * @return The language of the query, null if the language was not set.
     */
    public static String getQueryLang(IngridQuery query) {
        String result = null;

        FieldQuery[] qFields = query.getFields();
        for (int i = 0; i < qFields.length; i++) {
            final String fieldName = qFields[i].getFieldName();
            if (fieldName.equals("lang")) {
                result = qFields[i].getFieldValue();
            }
        }

        return result;
    }
    
    /**
     * Get a field from the json object within the data field of a codelist entry.
     * @param entry
     * @param field
     * @return
     */
    public static String getFieldFromData(CodeListEntry entry, String field) {
        String data = entry.getData();
        
        if (data == null) {
            log.warn( "No data field in codelist entry: " + entry.getId() );
            return "?";
        }
        
        try {
            JSONObject json = new JSONObject( data );
            return json.getString( field );
        } catch (JSONException e) {
            log.error( "Could not read id of partner in codelist", e );
            return "?";
        }
    }

}
