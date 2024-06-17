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
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.cat.indices.IndicesRecord;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import co.elastic.clients.elasticsearch.indices.get_mapping.IndexMappingRecord;
import co.elastic.clients.elasticsearch.indices.stats.IndexMetadataState;
import co.elastic.clients.util.DateTime;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import de.ingrid.elasticsearch.ElasticsearchNodeFactoryBean;
import de.ingrid.elasticsearch.IndexInfo;
import de.ingrid.elasticsearch.IndexManager;
import de.ingrid.elasticsearch.QueryBuilderService;
import de.ingrid.ibus.model.*;
import de.ingrid.utils.ElasticDocument;
import de.ingrid.utils.IngridHitDetail;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.terracotta.context.query.QueryBuilder;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        List<IndicesRecord> esIndices = null;
        try {
            esIndices = client.cat().indices().valueBody();
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
                addDefaultIndexInfo(indexMap.index(), index, indexMap.value.getSettings());

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
        addDefaultIndexInfo(indexId, index, getSettingsResponse.get(indexId));

        applyAdditionalData(indexId, index);

        applyDetailedIndexInfo(indexId, index);

        return index;
    }

    private void applyDetailedIndexInfo(String indexName, IndexTypeDetail index) {

        index.setType("base");

        addMapping(indexName, index);
    }

    private void addMapping(String indexName, Index index) {
        Map<String, IndexMappingRecord> mappings = null;
        try {
            mappings = client.indices().getMapping(m -> m.index(indexName)).result();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> indexMapping = new HashMap<>();
        Map<String, Property> properties = mappings.get(indexName).mappings().properties();
//        properties.forEach((key, value) -> {
//            try {
//                indexMapping.put(key, value);
//            } catch (Exception e) {
//                log.error("Error during setting of mapping in elasticsearch index", e);
//            }
//        });

        // TODO AW: index.setMapping(properties);

    }

    public void toggleIndexActiveState(String indexId, boolean active) {
        Query indexTypeQuery = queryBuilderService.buildMustQuery(INDEX_FIELD_INDEX_ID, indexId)._toQuery();

        SearchResponse<ElasticDocument> response = null;
        try {
            response = client.search(s -> s.index(INDEX_INFO_NAME)
                    .query(indexTypeQuery)
                    .source(fs -> fs.fetch(false))
                    .size(1), ElasticDocument.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String id = response.hits().hits().get(0).id();
//        UpdateRequest request = new UpdateRequest(INDEX_INFO_NAME, id);
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("active", active);
//        request.doc(jsonMap);
        client.update(u -> u
                .index(INDEX_INFO_NAME)
                .id(id)
                .doc(jsonMap));
    }

    private void addTypes(Index index) {
            List<IndexType> indexTypes = new ArrayList<>();
            IndexType newIndexType = new IndexType();
            newIndexType.setName("base");
            indexTypes.add(newIndexType);
            index.setTypes(indexTypes);
    }

    private void addDefaultIndexInfo(String indexName, Index index, Settings settings) {
        SearchResponse<ElasticDocument> searchResponse = null;
        try {
            searchResponse = client.search(s -> s
                    .index(indexName)
                            .size(0)
                    , ElasticDocument.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        String created = settings.get(IndexMetadataState.valueOf("").SETTING_CREATION_DATE);

        index.setName(indexName);
        index.setNumberDocs(searchResponse.hits().total().value());
        index.setCreated(new Date(Long.parseLong(created)));
    }

    /**
     * Request data from special collection in Elasticsearch, where additional metadata is stored about an index and the corresponding
     * iPlug, that delivers the data of the index.
     *
     * @param indexName is the name of the index
     * @param index detailed info about index
     */
    @SuppressWarnings("unchecked")
    private void applyAdditionalData(String indexName, IndexTypeDetail index) {
        BoolQuery indexTypeQuery = queryBuilderService.buildMustQuery(LINKED_INDEX, indexName);

        SearchResponse response = null;
        try {
            response = client.search(s->s
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
            Map<String, Object> hitSource = response.hits().hits().get(0);

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
        SearchResponse response = null;
        try {
            response = client.search(s -> s.index(INDEX_INFO_NAME)
                    .source( fs -> fs.fetch(true))// new String[]{"*"}, null)
                    .size(1000)
                    , ElasticDocument.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Hit> hits = response.hits().hits();

        // iterate over all index informations and apply info to Index-object
        for (Hit hit : hits) {
            Map<String, Object> hitSource = hit.fields();
            String indexName = (String) hit.fields().get(LINKED_INDEX);

            try {
                Index indexItem = indices.stream()
                        .filter(index -> index.getName().equals(indexName))
                        .findFirst()
                        .orElse(null);

                if (indexItem != null) {
                    StdDateFormat format = new StdDateFormat();
                    String lastIndexedString = (String) hit.fields().get(INDEX_FIELD_LAST_INDEXED);
                    Date lastIndexed = null;
                    if (lastIndexedString != null) lastIndexed = format.parse(lastIndexedString);


                    indexItem.setId(hit.id());
                    indexItem.setLongName((String) hitSource.get(INDEX_FIELD_IPLUG_NAME));

                    // check if linked component / iPlug is connected
                    boolean iPlugIsConnected = iPlugService.isConnectedDirectly((String) hitSource.get("plugId"));
                    indexItem.setConnected(iPlugIsConnected);

                    indexItem.setHasLinkedComponent(true);
                    indexItem.setAdminUrl((String) ((Map) hitSource.get("plugdescription")).get("IPLUG_ADMIN_GUI_URL"));

                    for (IndexType type : indexItem.getTypes()) {
                        type.setId((String) hitSource.get(INDEX_FIELD_INDEX_ID));
                        type.setHasLinkedComponent(true);
                        type.setLastIndexed(lastIndexed);
                        type.setActive(settingsService.isActive(type.getId()));
                    }
                }
            } catch (Exception ex) {
                // skip and continue
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
            return new DateTime(date).toDate();
        } else {
            return null;
        }
    }

    // public List<SearchResult> search(String query) {
    //
    // IngridQuery iQuery = new IngridQuery( true, false, 0, query );
    // IngridHits searchAndDetail = searchService.searchAndDetail( iQuery, 5, 0, 0, 1000, new String[] { "title" } );
    //
    // String[] indices = getActiveIndices();
    // String[] justIndexNames = Stream.of( indices )
    // .map( indexWithType -> indexWithType.split( ":" )[0] )
    // .collect( Collectors.toSet() )
    // .toArray( new String[0] );
    //
    // BoolQueryBuilder indexTypeFilter = queryBuilderService.createIndexTypeFilter( indices );
    // BoolQueryBuilder queryWithFilter = queryBuilderService.createQueryWithFilter( query, indexTypeFilter );
    //
    // SearchResponse response = client.prepareSearch( justIndexNames )
    // .setQuery( queryWithFilter )
    // .setFetchSource( new String[] { "*" }, null )
    // .setSize( 10 )
    // .get();
    //
    // SearchHit[] hits = response.getHits().getHits();
    //
    // List<SearchResult> results = new ArrayList<SearchResult>();
    // for (SearchHit hit : hits) {
    // SearchResult result = new SearchResult();
    // result.setId( hit.getId() );
    // result.setIndexId( hit.getIndex() );
    // result.setTitle( (String) hit.getSource().get( "title" ) );
    // result.setSummary( (String) hit.getSource().get( "summary" ) );
    // result.setSource( (String) hit.getSource().get( "name" ) );
    // results.add( result );
    // }
    // return results;
    // }

    public HitsMetadata<ElasticDocument> search(QueryBuilder query) {
        IndexInfo[] indices = getActiveIndices();

        // when no index was selected then do not return any hits
        if (indices.length == 0) {
            return HitsMetadata.of( b -> b
                    .hits(new ArrayList<>())
                    .total(TotalHits.of(th -> th
                        .relation(TotalHitsRelation.Eq)
                        .value(0))));
        }

        String[] justIndexNames = Stream.of(indices)
                .map(IndexInfo::getToIndex)
                .distinct()
                .toArray(String[]::new);

        BoolQuery.Builder indexTypeFilter = queryBuilderService.createIndexTypeFilter(indices);

        // TODO: handle not existing index names which lead to an exception

        SearchResponse response = client.search(s -> s
                        .index(justIndexNames)
                .query(QueryBuilders.bool().must(query).must(indexTypeFilter))
                .source(fs -> fs.fetch(true)) // new String[]{"*"}, null)
                .size(10)
                , ElasticDocument.class);

        return response.hits();
    }

    public IndexInfo[] getActiveIndices() {
        List<IndexInfo> result = new ArrayList<>();

        // get active components
        Set<String> activeComponents = settingsService.getActiveComponentIds();

        if (activeComponents == null || activeComponents.isEmpty()) {
            return new IndexInfo[0];
        }

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        for (String active : activeComponents) {
            boolQuery.should(QueryBuilders.termQuery(INDEX_FIELD_INDEX_ID, active));
        }

        // get real index names from active components
        SearchResponse response = client.prepareSearch(INDEX_INFO_NAME)
                .setQuery(boolQuery)
                .setFetchSource(new String[]{LINKED_INDEX}, null)
                .setSize(1000)
                .get();

        // collect all referenced indices
        response.getHits().forEach(hit -> {
            String index = (String) hit.getSourceAsMap().get(LINKED_INDEX);
            if (index != null) {
                IndexInfo info = new IndexInfo();
                info.setToIndex(index);
                result.add(info);
            }
        });

        return result.toArray(new IndexInfo[0]);
    }

    public IngridHitDetail getHitDetail(String indexId, String hitId) {
        GetResponse response = client.prepareGet(indexId, null, hitId)
                .setFetchSource("*", null)
                .get();

        return mapHitDetail(response);
    }

    private IngridHitDetail mapHitDetail(GetResponse hit) {
        Map<String, Object> source = hit.getSource();

        String iPlugIdString;
        Object iPlugId = source.get("iPlugId");

        if (iPlugId instanceof ArrayList) {
            iPlugIdString = (String) ((ArrayList) iPlugId).get(0);
        } else {
            iPlugIdString = (String) iPlugId;
        }

        IngridHitDetail result = new IngridHitDetail(
                iPlugIdString,
                hit.getId(),
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
        client.admin().indices().prepareDelete(id).get();
    }

    public String getIPlugForIndex(String id) {
        SearchResponse response = client.prepareSearch(INDEX_INFO_NAME)
                .setQuery(QueryBuilders.termQuery(LINKED_INDEX, id))
                .setFetchSource(new String[]{"*"}, null)
                .setSize(1000)
                .get();

        SearchHit[] hits = response.getHits().getHits();

        // get first plugid found
        // it's possible that there are more than one documents returned since for each type a document exists
        if (hits.length > 0) {
            return (String) hits[0].getSourceAsMap().get(INDEX_FIELD_IPLUG_ID);
        } else {
            log.error("There should be at least one corresponding component for the index: " + id);
        }
        return null;
    }

    public void prepareIndices() {
        indexManager.checkAndCreateInformationIndex();
    }

    public List<ConfigIndexEntry> getConfigurationIndexEntries() {
        SearchResponse response = client.prepareSearch(INDEX_INFO_NAME)
                .setFetchSource(new String[]{"*"}, null)
                .setSize(1000)
                .get();

        SearchHit[] hits = response.getHits().getHits();
        return Arrays.stream(hits)
                .map(this::mapHitToConfigIndexEntry)
                .collect(Collectors.toList());
    }

    private ConfigIndexEntry mapHitToConfigIndexEntry(SearchHit hit) {

        ConfigIndexEntry entry = new ConfigIndexEntry();
        Map<String, Object> source = hit.getSourceAsMap();

        entry.id = hit.getId();
        entry.plugId = (String) source.get("plugId");
        entry.indexId = (String) source.get("indexId");
        entry.lastHeartbeat = (String) source.get("lastHeartbeat");
        entry.iPlugName = (String) source.get("iPlugName");
        entry.linkedType = (String) source.get("linkedType");
        entry.lastIndexed = (String) source.get("lastIndexed");
        entry.linkedIndex = (String) source.get("linkedIndex");
        entry.adminUrl = (String) source.get("adminUrl");
        entry.plugdescription = (Map<String, Object>) source.get("plugdescription");

        return entry;
    }

    public void removeConfigurationIndexEntry(String id) {
        DeleteRequest request = new DeleteRequest(INDEX_INFO_NAME)
                .type("info")
                .id(id);
        client.delete(request).actionGet();
    }

    public void removeConfigurationIndex() {
        deleteIndex(INDEX_INFO_NAME);
        indexManager.checkAndCreateInformationIndex();
    }
}
