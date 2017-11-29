/*
 * **************************************************-
 * Ingrid Management iPlug
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
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.ibus.management;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.codelists.CodeListService;
import de.ingrid.codelists.model.CodeList;
import de.ingrid.codelists.util.CodeListUtils;
import de.ingrid.ibus.management.usecase.ManagementAuthenticationUseCase;
import de.ingrid.ibus.management.usecase.ManagementGetPartnerUseCase;
import de.ingrid.ibus.management.usecase.ManagementGetProviderAsListUseCase;
import de.ingrid.ibus.management.usecase.ManagementUseCase;
import de.ingrid.ibus.management.utils.ManagementUtils;
import de.ingrid.iplug.HeartBeatPlug;
import de.ingrid.utils.IngridCall;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
@Service
public class ManagementIPlug extends HeartBeatPlug implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4039600357553109931L;

    public static final String DATATYPE_MANAGEMENT = "management";

    public static final String MANAGEMENT_REQUEST_TYPE = "management_request_type";

    /**
     * The logging object
     */
    private static Log log = LogFactory.getLog(ManagementIPlug.class);

    /**
     * The <code>PlugDescription</code> object passed at startup
     */
    private PlugDescription fPlugDesc = null;

    /**
     * Unique Plug-iD
     */
    private String fPlugId = null;

    // injected by Spring
    @Autowired
    private CodeListService codeListService;

    private static final int MANAGEMENT_AUTHENTICATE = 0;

    private static final int MANAGEMENT_GET_PARTNERS = 1;

    private static final int MANAGEMENT_GET_PROVIDERS_AS_LIST = 2;
    
    private static final int MANAGEMENT_GET_CODELISTS_AS_LIST = 3;

    public ManagementIPlug() {
        super(30000, null, null, null, null);
    };
    
    @PostConstruct
    public void init() {
        List<CodeList> list = codeListService.getCodeLists();
        
//        String data = list.get(66).getEntries().get(0).getData();
//        JSONObject json = new JSONObject( data );
//        json.get( "id" );
    }
    
//    @Autowired
//    public ManagementIPlug(IMetadataInjector[] injector, IPreProcessor[] preProcessors, IPostProcessor[] postProcessors) {
//        super(30000, new PlugDescriptionFieldFilters(), injector, preProcessors, postProcessors);
//    }
    
    /**
     * @see de.ingrid.utils.IPlug#configure(de.ingrid.utils.PlugDescription)
     */
    @Override
    public void configure(PlugDescription plugDescription) {
        super.configure(plugDescription);
        log.info("Configuring Management-iPlug...");

        this.fPlugDesc = plugDescription;
        this.fPlugId = fPlugDesc.getPlugId();
    }

    /**
     * @see de.ingrid.utils.IPlug#close()
     */
    public void close() throws Exception {
        // TODO Auto-generated method stub

    }

    /**
     * @see de.ingrid.utils.ISearcher#search(de.ingrid.utils.query.IngridQuery,
     *      int, int)
     */
    public IngridHits search(IngridQuery query, int start, int length) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("incomming query : " + query.toString());
        }
        if (ManagementUtils.containsManagementDataType(query.getDataTypes())) {
            int type = -1;
            try {
                type = Integer.parseInt(ManagementUtils.getField(query, MANAGEMENT_REQUEST_TYPE));
            } catch (NumberFormatException e) {
            }
            int totalSize = 0;
            try {
                IngridHit[] hitsTemp = null;
                ManagementUseCase uc = null;

                switch (type) {
                // authenticate a user
                case MANAGEMENT_AUTHENTICATE:

                    uc = new ManagementAuthenticationUseCase();
                    // execute use case
                    hitsTemp = uc.execute(query, start, length, this.fPlugId);

                    break;

                // return partner / provider hierarchy
                case MANAGEMENT_GET_PARTNERS:

                    uc = new ManagementGetPartnerUseCase(codeListService);
                    // execute use case
                    hitsTemp = uc.execute(query, start, length, this.fPlugId);

                    break;

                // return provider list
                case MANAGEMENT_GET_PROVIDERS_AS_LIST:

                    uc = new ManagementGetProviderAsListUseCase(codeListService);
                    // execute use case
                    hitsTemp = uc.execute(query, start, length, this.fPlugId);

                    break;
                case MANAGEMENT_GET_CODELISTS_AS_LIST:
                    // TODO: use caching?
                    List<CodeList> modifiedCodelists = codeListService.updateFromServer(extractLastModifiedTimestamp(query));
                    hitsTemp = new IngridHit[1];
                    IngridHit hit = new IngridHit(this.fPlugId, "0", 0, 1.0f);
                    hitsTemp[0] = hit;
                    if (modifiedCodelists != null) {
                        hitsTemp[0].put("codelists", CodeListUtils.getXmlFromObject(modifiedCodelists));
                    }
                    break;
                default:
                    log.error("Unknown management request type.");
                    break;
                }

                IngridHit[] hits = new IngridHit[0];
                if (null != hitsTemp) {
                    hits = hitsTemp;
                }

                start = 0;

                int max = Math.min((hits.length - start), length);
                IngridHit[] finalHits = new IngridHit[max];
                System.arraycopy(hits, start, finalHits, 0, max);
                totalSize = max;
                if (log.isDebugEnabled()) {
                    log.debug("hits: " + totalSize);
                }

                if ((0 == totalSize) && (hits.length > 0)) {
                    totalSize = hits.length;
                }
                return new IngridHits(this.fPlugId, totalSize, finalHits, false);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else {
            if (log.isErrorEnabled()) {
                log.error("not correct or unsetted datatype");
            }
        }
        return new IngridHits(this.fPlugId, 0, new IngridHit[0], true);
    }

    private Long extractLastModifiedTimestamp(IngridQuery query) {
        FieldQuery[] fields = query.getFields();
        for (FieldQuery field : fields) {
            if (field.containsValue("lastModified")) {
                return Long.valueOf(field.getFieldValue()); 
            }
        }
        return -1L;
    }

    /**
     * @see de.ingrid.utils.IDetailer#getDetail(de.ingrid.utils.IngridHit,
     *      de.ingrid.utils.query.IngridQuery, java.lang.String[])
     */
    public IngridHitDetail getDetail(IngridHit hit, IngridQuery query, String[] requestedFields) throws Exception {
        return new IngridHitDetail();
    }

    /**
     * @see de.ingrid.utils.IDetailer#getDetails(de.ingrid.utils.IngridHit[],
     *      de.ingrid.utils.query.IngridQuery, java.lang.String[])
     */
    public IngridHitDetail[] getDetails(IngridHit[] hits, IngridQuery query, String[] requestedFields) throws Exception {
        return new IngridHitDetail[0];
    }

    
    public void setCodeListService(CodeListService codeListService) {
        this.codeListService = codeListService;
    }

    @Override
    public IngridDocument call(IngridCall targetInfo) throws Exception {
        throw new RuntimeException( "call-function not implemented in Management-iPlug" );
    }
}
