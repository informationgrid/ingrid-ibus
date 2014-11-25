/*
 * **************************************************-
 * InGrid iBus
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
/**
 * 
 */
package de.ingrid.ibus.processor;

import java.net.URL;
import java.util.Iterator;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.utils.processor.IPreProcessor;
import de.ingrid.utils.query.ClauseQuery;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.query.TermQuery;
import de.ingrid.utils.queryparser.QueryStringParser;
import de.ingrid.utils.tool.QueryUtil;

/**
 * This pre processor replaces terms and fields in a query with a sub query. The
 * replacements can be defined in a property file
 * (classpath:/querymodifier.properties). The property file will be reloaded
 * every 5 sec.
 * 
 * @author joachim@wemove.com
 * 
 */
public class QueryModifierPreProcessor implements IPreProcessor {

    private static Log fLogger = LogFactory.getLog(QueryModifierPreProcessor.class);

    PropertiesConfiguration config = null;

    public QueryModifierPreProcessor() {
    }

    public QueryModifierPreProcessor(String queryModifierProperties) {
        setConfiguration(queryModifierProperties);
    }

    public void setConfiguration(String queryModifier) {
        URL url = null;
        try {
            url = this.getClass().getResource(queryModifier);
            config = new PropertiesConfiguration(url);
            config.setReloadingStrategy(new FileChangedReloadingStrategy());
        } catch (ConfigurationException e) {
            fLogger.error("Error loading querymodifier.properties from " + url, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ingrid.utils.processor.IPreProcessor#process(de.ingrid.utils.query
     * .IngridQuery)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void process(IngridQuery query) throws Exception {
        if (fLogger.isDebugEnabled()) {
            fLogger.debug("in:" + QueryUtil.query2String(query));
            for (Iterator<String> it = config.getKeys(); it.hasNext();) {
                String key = it.next();
                fLogger.debug("map key: '" + key + "' to value: '" + config.getString(key) + "'");
            }
        }
        for (IngridQuery clause : query.getAllClauses()) {
            for (TermQuery term : clause.getTerms()) {
                if (config.containsKey(term.getTerm())) {
                    String qStr = config.getString(term.getTerm()).trim();
                    IngridQuery q = QueryStringParser.parse(qStr);
                    clause.removeFromList(IngridQuery.TERM_KEY, term);
                    ClauseQuery modifierClause = new ClauseQuery(term.isRequred(), term.isProhibited());
                    modifierClause.putAll(q);
                    clause.addClause(modifierClause);
                }
            }
            for (FieldQuery field : clause.getFields()) {
                if (config.containsKey(field.getFieldValue())) {
                    String qStr = config.getString(field.getFieldName() + ":" + field.getFieldValue()).trim();
                    IngridQuery q = QueryStringParser.parse(qStr);
                    clause.removeField(field);
                    ClauseQuery modifierClause = new ClauseQuery(field.isRequred(), field.isProhibited());
                    modifierClause.putAll(q);
                    clause.addClause(modifierClause);
                }
            }
        }
        if (fLogger.isDebugEnabled()) {
            fLogger.debug("out:" + QueryUtil.query2String(query));
        }

    }

}
