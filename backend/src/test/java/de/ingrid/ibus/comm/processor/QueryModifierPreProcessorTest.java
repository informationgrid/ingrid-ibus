/*
 * **************************************************-
 * InGrid iBus
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
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
package de.ingrid.ibus.comm.processor;

import de.ingrid.ibus.comm.processor.QueryModifierPreProcessor;

import org.junit.jupiter.api.Test;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;
import de.ingrid.utils.tool.QueryUtil;

public class QueryModifierPreProcessorTest {

    @Test
    public void testProcess() throws Exception {
        QueryModifierPreProcessor modifier = new QueryModifierPreProcessor("/querymodifier.properties");

        IngridQuery q = QueryStringParser.parse("wasser Havelhochwasser cola bier (drink:cola OR drink:water -(oft \"nicht oft\"))");

        System.out.println("before: " + QueryUtil.query2String(q));
        modifier.process(q);
        System.out.println("after: " + QueryUtil.query2String(q));
        
        IngridQuery q1 = QueryStringParser.parse("Elbhochwasser OR (10 Jahre Elbehochwasser) OR (10 Jahre Elbe-Hochwasser) OR (Elbe-Hochwasser 2002) OR Elbehochwasser");
        IngridQuery q2 = QueryStringParser.parse("Elbhochwasser");
        modifier.process(q2);
        System.out.println("query without modifier: " + QueryUtil.query2String(q1));
        System.out.println("query with    modifier: " + QueryUtil.query2String(q2));
        
        
        
    }

}
