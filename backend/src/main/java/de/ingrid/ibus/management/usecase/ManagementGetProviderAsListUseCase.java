/*
 * **************************************************-
 * Ingrid Management iPlug
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
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
package de.ingrid.ibus.management.usecase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.codelists.CodeListService;
import de.ingrid.codelists.model.CodeList;
import de.ingrid.codelists.model.CodeListEntry;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.query.IngridQuery;

/**
 * Delivers all providers in the following structure. (ArrayList) partners
 * (HashMap)partner {partnerid="bund", providers=(ArrayList)} (ArrayList)
 * providers (HashMap) provider {providerid="bu_bfn", name="name of provider",
 * url="url of the provider"}
 * 
 * @author joachim@wemove.com
 */
public class ManagementGetProviderAsListUseCase implements ManagementUseCase {

    private static final Log log = LogFactory.getLog(ManagementGetProviderAsListUseCase.class);
    private CodeListService codelistService;

    /**
     * 
     */
    public ManagementGetProviderAsListUseCase(CodeListService codelistService) {
        
        this.codelistService = codelistService;
    }

    /**
     * @see de.ingrid.ibus.management.usecase.ManagementUseCase#execute(de.ingrid.utils.query.IngridQuery,
     *      int, int, java.lang.String)
     */
    public IngridHit[] execute(IngridQuery query, int start, int length, String plugId) {

        IngridHit[] result = null;
        List<Map<String, Object>> providerList = new ArrayList<Map<String, Object>>();
        
        CodeList providers = codelistService.getCodeList( "111" );

        for (CodeListEntry entry : providers.getEntries()) {
            
            Map<String, Object> providerHash = new HashMap<String, Object>();
            providerHash.put("providerid", entry.getField( "ident" ));
            providerHash.put("name", entry.getField( "name" ));
            providerHash.put("url", entry.getField( "url" ));
            providerList.add(providerHash);
        }

        IngridHit hit = new IngridHit(plugId, "0", 0, 1.0f);
        hit.put("provider", providerList);
        result = new IngridHit[1];
        result[0] = hit;

        return result;
    }

}
