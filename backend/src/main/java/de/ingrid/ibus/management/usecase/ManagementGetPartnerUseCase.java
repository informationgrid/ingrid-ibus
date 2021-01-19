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
 * Delivers all partners and providers in the following structure. (ArrayList) partners (HashMap)partner {partnerid="bund",
 * providers=(ArrayList)} (ArrayList) providers (HashMap) provider {providerid="bu_bfn"}
 * 
 * @author joachim@wemove.com
 */
public class ManagementGetPartnerUseCase implements ManagementUseCase {

    private static final Log log = LogFactory.getLog( ManagementGetPartnerUseCase.class );
    private CodeListService codelistService;

    /**
     *
     * 
     */
    public ManagementGetPartnerUseCase(CodeListService codelistService) {

        this.codelistService = codelistService;
    }

    /**
     * @see de.ingrid.ibus.management.usecase.ManagementUseCase#execute(de.ingrid.utils.query.IngridQuery, int, int, java.lang.String)
     */
    public IngridHit[] execute(IngridQuery query, int start, int length, String plugId) {

        IngridHit[] result;
        List<Map<String, Object>> partnerList = new ArrayList<>();

        CodeList partners = codelistService.getCodeList( "110" );
        CodeList providers = codelistService.getCodeList( "111" );

        List<CodeListEntry> entries = partners.getEntries();

        for (CodeListEntry entry : entries) {

            String partnerId = entry.getField( "ident" ); //codelistService.getManagementUtils.getFieldFromData( entry, "id" );
            if ("bund".equals( partnerId )) partnerId = "bu";
            
            Map<String, Object> partnerHash = mapPartner( entry );

            // add corresponding providers
            partnerHash.put( "providers", mapProviders( providers, partnerId ) );

            partnerList.add( partnerHash );

        }

        IngridHit hit = new IngridHit( plugId, "0", 0, 1.0f );
        hit.put( "partner", partnerList );
        result = new IngridHit[1];
        result[0] = hit;

        return result;
    }

    private Map<String, Object> mapPartner(CodeListEntry entry) {
        Map<String, Object> partnerHash = new HashMap<>();
        partnerHash.put( "partnerid", entry.getField( "ident" ) );
        partnerHash.put( "name", entry.getField( "name" ) );
        return partnerHash;
    }

    private List<Map<String, Object>> mapProviders(CodeList providers, String partnerId) {
        List<Map<String, Object>> providerList = new ArrayList<>();
        for (CodeListEntry provider : providers.getEntries()) {
            String providerId = provider.getField( "ident" );

            // get all providers that start with the partner ID
            if (providerId.startsWith( partnerId + "_" )) {
                Map<String, Object> providerHash = new HashMap<>();
                providerHash.put( "providerid", providerId );
                providerHash.put( "name", provider.getField( "name" ) );
                providerHash.put( "url", provider.getField( "url" ) );
                providerList.add( providerHash );
            }
        }
        return providerList;
    }

}
