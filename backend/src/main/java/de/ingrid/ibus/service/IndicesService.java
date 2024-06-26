/*-
 * **************************************************-
 * ingrid-ibus-backend
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.ibus.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.cat.indices.IndicesRecord;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.GetIndicesSettingsResponse;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.elasticsearch.indices.get_mapping.IndexMappingRecord;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import de.ingrid.elasticsearch.ElasticsearchNodeFactoryBean;
import de.ingrid.elasticsearch.IndexInfo;
import de.ingrid.elasticsearch.IndexManager;
import de.ingrid.elasticsearch.QueryBuilderService;
import de.ingrid.ibus.model.*;
import de.ingrid.utils.ElasticDocument;
import de.ingrid.utils.IngridHitDetail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IndicesService {

    private static final String LINKED_INDEX = "linkedIndex";
    private static final String INDEX_FIELD_LAST_INDEXED = "lastIndexed";
    private static final String INDEX_FIELD_INDEXING_STATE = "indexingState";
    private static final String INDEX_FIELD_ADMIN_URL = "adminUrl";
    private static final String INDEX_FIELD_LAST_HEARTBEAT = "lastHeartbeat";
    private static final String INDEX_FIELD_IPLUG_ID = "plugId";
    private static final String INDEX_FIELD_INDEX_ID = "indexId";
    private static final String INDEX_FIELD_IPLUG_NAME = "iPlugName";
    private static final String INDEX_INFO_NAME = "ingrid_meta";

    private final Logger log = LogManager.getLogger(IndicesService.class);

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private IndexManager indexManager;

    @Autowired
    private QueryBuilderService queryBuilderService;

    @Autowired
    private ElasticsearchNodeFactoryBean esBean;

    @Autowired
    private IPlugService iPlugService;

    private ElasticsearchClient client;

    @Value("${index.prefix.filter:}")
    private String indexPrefixFilter;

    public IndicesService() {
    }

    @PostConstruct
    public void init() {
        client = esBean.getClient();
        try {
            prepareIndices();
        } catch (Exception ex) {
            log.warn("Could not connect to elasticsearch node");
        }
    }


    @PreDestroy
    public void destroy() {
        client.shutdown();
    }

    public ElasticsearchInfo getElasticsearchInfo() {
        ElasticsearchInfo info = new ElasticsearchInfo();
        List<Index> indices = new ArrayList<>();

        List<IndicesRecord> esIndices;
        GetIndicesSettingsResponse settings;
        try {
            esIndices = client.cat().indices().valueBody();
            settings = client.indices().getSettings();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        esIndices.forEach((indexMap) -> {
            // System.out.println( "Index: " + indexMap.key );

            // skip indices that do not start with the configured prefix, since we don't want to have all indices of a cluster
            if (!indexMap.index().startsWith(indexPrefixFilter)) {
                return;
            }

            Index index = new Index();

            try {
                addDefaultIndexInfo(indexMap.index(), index, settings.get(indexMap.index()).settings());

                // applyAdditionalData( indexMap.key, index, false );
                // addMapping( indexMap.key, null, index );

                addTypes(index);

                // check if iPlug is connected through InGrid Communication
                //iPlugService.getIPlugDetail()

                indices.add(index);
            } catch (Exception ex) {
                log.warn("Could not get index, since it is closed: " + indexMap.index());
            }
        });

        addComponentData(indices);


        info.setIndices(indices);

        return info;
    }

    public IndexTypeDetail getIndexDetail(String indexId) {
        IndexTypeDetail index = new IndexTypeDetail();

        Map<String, co.elastic.clients.elasticsearch.indices.IndexState> getSettingsResponse = null;
        try {
            getSettingsResponse = client.indices().getSettings(s -> s.index(indexId)).result();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        addDefaultIndexInfo(indexId, index, getSettingsResponse.get(indexId).settings());

        applyAdditionalData(indexId, index);

        applyDetailedIndexInfo(indexId, index);

        return index;
    }

    private void applyDetailedIndexInfo(String indexName, IndexTypeDetail index) {
        addMapping(indexName, index);
    }

    private void addMapping(String indexName, Index index) {
        Map<String, IndexMappingRecord> mappings;
        try {
            mappings = client.indices().getMapping(m -> m.index(indexName)).result();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String mappingAsString = mappings.get(indexName).mappings().properties()
                .toString()
                .replaceAll("=Property", "");

        try {
            index.setMapping(new JSONObject(mappingAsString).toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public void toggleIndexActiveState(String indexId, boolean active) {
        Query indexTypeQuery = queryBuilderService.buildMustQuery(INDEX_FIELD_INDEX_ID, indexId)._toQuery();

        SearchResponse<ElasticDocument> response;
        try {
            response = client.search(s -> s.index(INDEX_INFO_NAME)
                    .query(indexTypeQuery)
                    .source(fs -> fs.fetch(false))
                    .size(1), ElasticDocument.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String id = response.hits().hits().get(0).id();
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("active", active);
        try {
            client.update(u -> u
                    .index(INDEX_INFO_NAME)
                    .id(id)
                    .doc(jsonMap), ElasticDocument.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addTypes(Index index) {
        List<IndexType> indexTypes = new ArrayList<>();
        IndexType newIndexType = new IndexType();
        newIndexType.setName("base");
        indexTypes.add(newIndexType);
        index.setTypes(indexTypes);
    }

    private void addDefaultIndexInfo(String indexName, Index index, IndexSettings settings) {
        SearchResponse<ElasticDocument> searchResponse;
        try {
            searchResponse = client.search(s -> s
                            .index(indexName)
                            .size(0)
                    , ElasticDocument.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        Long created = settings.index().creationDate();

        index.setName(indexName);
        index.setNumberDocs(searchResponse.hits().total().value());
        index.setCreated(new Date(created));
    }

    /**
     * Request data from special collection in Elasticsearch, where additional metadata is stored about an index and the corresponding
     * iPlug, that delivers the data of the index.
     *
     * @param indexName is the name of the index
     * @param index     detailed info about index
     */
    @SuppressWarnings("unchecked")
    private void applyAdditionalData(String indexName, IndexTypeDetail index) {
        BoolQuery indexTypeQuery = queryBuilderService.buildMustQuery(LINKED_INDEX, indexName);

        SearchResponse<ElasticDocument> response;
        try {
            response = client.search(s -> s
                            .index(INDEX_INFO_NAME)
                            .query(indexTypeQuery._toQuery())
                            .source(fs -> fs.fetch(true))
                            .size(1)
                    , ElasticDocument.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        long totalHits = response.hits().total().value();
        if (totalHits == 1) {
            Map<String, Object> hitSource = response.hits().hits().get(0).source();

            index.setId((String) hitSource.get(INDEX_FIELD_INDEX_ID));
            index.setPlugId((String) hitSource.get(INDEX_FIELD_IPLUG_ID));
            index.setLongName((String) hitSource.get(INDEX_FIELD_IPLUG_NAME));
            index.setLastIndexed(mapDate((String) hitSource.get(INDEX_FIELD_LAST_INDEXED)));
            index.setActive(settingsService.isActive(index.getId()));

            index.setLastHeartbeat(mapDate(hitSource.get(INDEX_FIELD_LAST_HEARTBEAT).toString()));
            index.setAdminUrl(hitSource.get(INDEX_FIELD_ADMIN_URL).toString());
            index.setIndexingState(mapIndexingState((Map<String, Object>) hitSource.get(INDEX_FIELD_INDEXING_STATE)));
            // index.setHasLinkedComponent( true );

        } else {
            // throw new RuntimeException( "Number of iPlugInfo-Hits should be 1, but was: " + totalHits );
            log.error("Number of iPlugInfo-Hits should be 1, but was: " + totalHits);
        }

    }

    private void addComponentData(List<Index> indices) {
        SearchResponse<ElasticDocument> response;
        try {
            response = client.search(s -> s.index(INDEX_INFO_NAME)
                            .source(fs -> fs.fetch(true))// new String[]{"*"}, null)
                            .size(1000)
                    , ElasticDocument.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Hit<ElasticDocument>> hits = response.hits().hits();

        // iterate over all index informations and apply info to Index-object
        for (Hit<ElasticDocument> hit : hits) {
            Map<String, Object> hitSource = hit.source();
            String indexName = (String) hitSource.get(LINKED_INDEX);

            try {
                Index indexItem = indices.stream()
                        .filter(index -> index.getName().equals(indexName))
                        .findFirst()
                        .orElse(null);

                if (indexItem != null) {
                    StdDateFormat format = new StdDateFormat();
                    String lastIndexedString = (String) hitSource.get(INDEX_FIELD_LAST_INDEXED);
                    Date lastIndexed = null;
                    if (lastIndexedString != null) lastIndexed = format.parse(lastIndexedString);


                    indexItem.setId(hit.id());
                    indexItem.setLongName((String) hitSource.get(INDEX_FIELD_IPLUG_NAME));

                    // check if linked component / iPlug is connected
                    boolean iPlugIsConnected = iPlugService.isConnectedDirectly((String) hitSource.get("plugId"));
                    indexItem.setConnected(iPlugIsConnected);

                    indexItem.setHasLinkedComponent(true);
                    indexItem.setAdminUrl((String) ((Map) hitSource.get("plugdescription")).get("IPLUG_ADMIN_GUI_URL"));

                    if (!indexItem.getTypes().isEmpty()) {
                        IndexType type = indexItem.getTypes().get(0);
                        type.setId((String) hitSource.get(INDEX_FIELD_INDEX_ID));
                        type.setHasLinkedComponent(true);
                        type.setLastIndexed(lastIndexed);
                        type.setActive(settingsService.isActive(type.getId()));
                    }
                }
            } catch (Exception ex) {
                // skip and continue
                log.debug("Skipping index: " + ex.getMessage());
            }
        }
    }

    private IndexState mapIndexingState(Map<String, Object> state) {
        IndexState indexState = new IndexState();

        Integer numProcessed = (Integer) state.get("numProcessed");
        Integer totalDocs = (Integer) state.get("totalDocs");

        // indexState.setMessage( message );
        indexState.setRunning(state.get("running").equals(true));
        if (numProcessed != null) {
            indexState.setNumProcessed(numProcessed);
        }
        if (totalDocs != null) {
            indexState.setTotalDocs(Integer.parseInt(state.get("totalDocs").toString()));
        }
        return indexState;
    }

    private Date mapDate(String date) {
        if (date != null) {
            try {
                return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse(date);
            } catch (ParseException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public IndexInfo[] getActiveIndices() {
        List<IndexInfo> result = new ArrayList<>();

        // get active components
        Set<String> activeComponents = settingsService.getActiveComponentIds();

        if (activeComponents == null || activeComponents.isEmpty()) {
            return new IndexInfo[0];
        }

        BoolQuery.Builder boolQuery = QueryBuilders.bool();

        for (String active : activeComponents) {
            boolQuery.should(TermQuery.of(tq -> tq.field(INDEX_FIELD_INDEX_ID).value(active))._toQuery());
        }

        // get real index names from active components
        SearchResponse<ElasticDocument> response;
        try {
            response = client.search(s -> s
                            .index(INDEX_INFO_NAME)
                            .query(boolQuery.build()._toQuery())
                            .source(fs -> fs.fetch(true)) //new String[]{LINKED_INDEX}, null)
                            .size(1000)
                    , ElasticDocument.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // collect all referenced indices
        response.hits().hits().forEach(hit -> {
            String index = (String) (hit.source() != null ? hit.source().get(LINKED_INDEX) : null);
            if (index != null) {
                IndexInfo info = new IndexInfo();
                info.setToIndex(index);
                result.add(info);
            }
        });

        return result.toArray(new IndexInfo[0]);
    }

    public IngridHitDetail getHitDetail(String indexId, String hitId) {
        GetResponse<ElasticDocument> response;
        try {
            response = client.get(g -> g
                            .index(indexId)
                            .id(hitId)
                            .source(fs -> fs.fetch(true))
                    //                .setFetchSource("*", null)
                    , ElasticDocument.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return mapHitDetail(response);
    }

    private IngridHitDetail mapHitDetail(GetResponse<ElasticDocument> hit) {
        Map<String, Object> source = hit.source();

        String iPlugIdString;
        Object iPlugId = source.get("iPlugId");

        if (iPlugId instanceof ArrayList) {
            iPlugIdString = (String) ((ArrayList<?>) iPlugId).get(0);
        } else {
            iPlugIdString = (String) iPlugId;
        }

        IngridHitDetail result = new IngridHitDetail(
                iPlugIdString,
                hit.id(),
                -1,
                -1.0f,
                (String) source.get("title"),
                (String) source.get("summary"));

        result.put("source", source.get("iPlugId"));
        result.put("idf", source.get("idf"));
        result.put("indexDoc", source);

        return result;
    }

    public void deleteIndex(String id) {
        try {
            client.indices().delete(d -> d.index(id));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getIPlugForIndex(String id) {
        SearchResponse<ElasticDocument> response;
        try {
            response = client.search(s -> s
                            .index(INDEX_INFO_NAME)
                            .query(TermQuery.of(tq -> tq.field(LINKED_INDEX).value(id))._toQuery())
                            .source(fs -> fs.fetch(true)) // new String[]{"*"}, null)
                            .size(1000)
                    , ElasticDocument.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Hit<ElasticDocument>> hits = response.hits().hits();

        // get first plugid found
        // it's possible that there are more than one documents returned since for each type a document exists
        if (!hits.isEmpty()) {
            return (String) hits.get(0).source().get(INDEX_FIELD_IPLUG_ID);
        } else {
            log.error("There should be at least one corresponding component for the index: " + id);
        }
        return null;
    }

    public void prepareIndices() {
        indexManager.checkAndCreateInformationIndex();
    }

    public List<ConfigIndexEntry> getConfigurationIndexEntries() {
        SearchResponse<ElasticDocument> response;
        try {
            response = client.search(s -> s
                            .index(INDEX_INFO_NAME)
                            .source(fs -> fs.fetch(true)) // .setFetchSource(new String[]{"*"}, null)
                            .size(1000)
                    , ElasticDocument.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Hit<ElasticDocument>> hits = response.hits().hits();
        return hits.stream()
                .map(this::mapHitToConfigIndexEntry)
                .collect(Collectors.toList());
    }

    private ConfigIndexEntry mapHitToConfigIndexEntry(Hit<ElasticDocument> hit) {

        ConfigIndexEntry entry = new ConfigIndexEntry();
        Map<String, Object> source = hit.source();

        entry.id = hit.id();
        entry.plugId = (String) source.get("plugId");
        entry.indexId = (String) source.get("indexId");
        entry.lastHeartbeat = String.valueOf(source.get("lastHeartbeat"));
        entry.iPlugName = (String) source.get("iPlugName");
        entry.lastIndexed = String.valueOf(source.get("lastIndexed"));
        entry.linkedIndex = (String) source.get("linkedIndex");
        entry.adminUrl = (String) source.get("adminUrl");
        entry.plugdescription = (Map<String, Object>) source.get("plugdescription");
        entry.active = (Boolean) source.get("active");

        return entry;
    }

    public void removeConfigurationIndexEntry(String id) {
        try {
            client.delete(d -> d.index(INDEX_INFO_NAME).id(id));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeConfigurationIndex() {
        deleteIndex(INDEX_INFO_NAME);
        indexManager.checkAndCreateInformationIndex();
    }
}
