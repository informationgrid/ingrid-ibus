package de.ingrid.ibus.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import de.ingrid.elasticsearch.ElasticsearchNodeFactoryBean;
import de.ingrid.elasticsearch.IndexManager;
import de.ingrid.elasticsearch.QueryBuilderService;
import de.ingrid.ibus.model.ElasticsearchInfo;
import de.ingrid.ibus.model.Index;
import de.ingrid.ibus.model.IndexState;
import de.ingrid.ibus.model.IndexType;
import de.ingrid.ibus.model.IndexTypeDetail;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.xml.XMLSerializer;

@Service
public class IndicesService {

    private static final String LINKED_INDEX = "linkedIndex";
    private static final String LINKED_TYPE = "linkedType";
    private static final String INDEX_FIELD_LAST_INDEXED = "lastIndexed";
    private static final String INDEX_FIELD_INDEXING_STATE = "indexingState";
    private static final String INDEX_FIELD_ADMIN_URL = "adminUrl";
    private static final String INDEX_FIELD_LAST_HEARTBEAT = "lastHeartbeat";
    private static final String INDEX_FIELD_IPLUG_ID = "plugId";
    private static final String INDEX_FIELD_INDEX_ID = "indexId";
    private static final String INDEX_FIELD_IPLUG_NAME = "iPlugName";
    private static final String INDEX_INFO_NAME = "ingrid_meta";

    private Logger log = LogManager.getLogger( IndicesService.class );

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

    private Client client;

    @Value("${index.prefix.filter:}")
    private String indexPrefixFilter;

    public IndicesService() throws IOException {
    }
    
    @PostConstruct
    public void init() {
        client = esBean.getClient();
        prepareIndices();
    }


    @PreDestroy
    public void destroy() {
        client.close();
    }

    /**
     * 
     * @return
     */
    public ElasticsearchInfo getElasticsearchInfo() {
        ElasticsearchInfo info = new ElasticsearchInfo();
        List<Index> indices = new ArrayList<Index>();

        try {
            ImmutableOpenMap<String, IndexMetaData> esIndices = client.admin().cluster().prepareState().execute().actionGet().getState().getMetaData().getIndices();
            esIndices.forEach( (indexMap) -> {
                System.out.println( "Index: " + indexMap.key );
    
                // skip indices that do not start with the configured prefix, since we don't want to have all indices of a cluster
                if (!indexMap.key.startsWith( indexPrefixFilter )) {
                    return;
                }
    
                Index index = new Index();
    
                addDefaultIndexInfo( indexMap.key, null, index, indexMap.value.getSettings() );
    
                // applyAdditionalData( indexMap.key, index, false );
                // addMapping( indexMap.key, null, index );
    
                addTypes( indexMap.key, index );
    
                // check if iPlug is connected through InGrid Communication
                //iPlugService.getIPlugDetail()
                
                indices.add( index );
            } );
    
            addComponentData( indices );
            
    
            info.setIndices( indices );
            
        } catch (Exception ex) {
            log.error( "Problem querying Elasticsearch:", ex );
        }

        return info;
    }

    /**
     * 
     * @param indexId
     * @param type
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public IndexTypeDetail getIndexDetail(String indexId, String type) throws InterruptedException, ExecutionException {
        IndexTypeDetail index = new IndexTypeDetail();

        GetSettingsResponse getSettingsResponse = client.admin().indices().prepareGetSettings( indexId ).execute().get();
        addDefaultIndexInfo( indexId, type, index, getSettingsResponse.getIndexToSettings().get( indexId ) );

        applyAdditionalData( indexId, type, index );

        applyDetailedIndexInfo( indexId, index, type );

        return index;
    }

    /**
     * 
     * @param indexName
     * @param index
     * @param type
     */
    private void applyDetailedIndexInfo(String indexName, IndexTypeDetail index, String type) {

        index.setType( type );

        addMapping( indexName, type, index );
    }

    /**
     * 
     * @param indexName
     * @param indexType
     * @param index
     */
    private void addMapping(String indexName, String indexType, Index index) {
        GetMappingsRequestBuilder rb = client.admin().indices().prepareGetMappings( indexName );

        if (indexType != null) {
            rb.setTypes( indexType );
        }

        GetMappingsResponse response = rb.execute().actionGet();

        ImmutableOpenMap<String, ImmutableOpenMap<String, MappingMetaData>> mappings = response.mappings();
        Map<String, Object> indexMapping = new HashMap<String, Object>();
        mappings.get( indexName ).forEach( (type -> {
            try {
                indexMapping.put( type.key, type.value.getSourceAsMap() );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }) );

        index.setMapping( indexMapping );

    }

    /**
     * 
     * @param indexName
     * @param index
     */
    private void addTypes(String indexName, Index index) {
        List<String> types = new ArrayList<String>();

        GetMappingsResponse response;
        try {
            response = client.admin().indices().prepareGetMappings( indexName ).execute().get();
            ImmutableOpenMap<String, ImmutableOpenMap<String, MappingMetaData>> mappings = response.mappings();
            mappings.get( indexName ).forEach( type -> {
                if (!"_default_".equals( type.key ))
                    types.add( type.key );
            } );

            List<IndexType> indexTypes = new ArrayList<IndexType>();
            for (String type : types) {
                IndexType newIndexType = new IndexType();
                newIndexType.setName( type );
                indexTypes.add( newIndexType );
            }
            index.setTypes( indexTypes );

        } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param indexName
     * @param type
     * @param index
     * @param settings
     */
    private void addDefaultIndexInfo(String indexName, String type, Index index, Settings settings) {
        SearchRequestBuilder srb = client.prepareSearch( indexName );

        if (type != null) {
            srb.setTypes( type );
        }

        SearchResponse searchResponse = srb.setSize( 0 ).get();

        String created = settings.get( IndexMetaData.SETTING_CREATION_DATE );

        index.setName( indexName );
        index.setNumberDocs( searchResponse.getHits().getTotalHits() );
        index.setCreated( new Date( Long.valueOf( created ) ) );
    }

    /**
     * Request data from special collection in Elasticsearch, where additional metadata is stored about an index and the corresponding
     * iPlug, that delivers the data of the index.
     * 
     * @param type
     * 
     * @param key
     * @param index
     */
    @SuppressWarnings("unchecked")
    private void applyAdditionalData(String indexName, String type, IndexTypeDetail index) {
        BoolQueryBuilder indexTypeQuery = queryBuilderService.buildMustQuery( LINKED_INDEX, indexName, LINKED_TYPE, type );

        SearchResponse response = client.prepareSearch( INDEX_INFO_NAME )
                .setTypes( "info" )
                .setQuery( indexTypeQuery )
                .setFetchSource( new String[] { "*" }, null )
                .setSize( 1 )
                .get();

        long totalHits = response.getHits().totalHits;
        if (totalHits == 1) {
            Map<String, Object> hitSource = response.getHits().getAt( 0 ).getSource();

            index.setId( (String) hitSource.get( INDEX_FIELD_INDEX_ID ) );
            index.setPlugId( (String) hitSource.get( INDEX_FIELD_IPLUG_ID ) );
            index.setLongName( (String) hitSource.get( INDEX_FIELD_IPLUG_NAME ) );
            index.setLastIndexed( mapDate( (String) hitSource.get( INDEX_FIELD_LAST_INDEXED ) ) );
            index.setActive( settingsService.isActive( index.getId() ) );

            index.setLastHeartbeat( mapDate( hitSource.get( INDEX_FIELD_LAST_HEARTBEAT ).toString() ) );
            index.setAdminUrl( hitSource.get( INDEX_FIELD_ADMIN_URL ).toString() );
            index.setIndexingState( mapIndexingState( (Map<String, Object>) hitSource.get( INDEX_FIELD_INDEXING_STATE ) ) );
            // index.setHasLinkedComponent( true );

        } else {
            // throw new RuntimeException( "Number of iPlugInfo-Hits should be 1, but was: " + totalHits );
            log.error( "Number of iPlugInfo-Hits should be 1, but was: " + totalHits );
        }

    }

    /**
     * 
     * @param indices
     */
    private void addComponentData(List<Index> indices) {
        SearchResponse response = client.prepareSearch( INDEX_INFO_NAME )
                .setTypes( "info" )
                .setFetchSource( new String[] { "*" }, null )
                .setSize( 1000 )
                .get();

        SearchHit[] hits = response.getHits().getHits();

        for (SearchHit hit : hits) {
            Map<String, Object> hitSource = hit.getSource();
            String indexName = (String) hit.getSource().get( LINKED_INDEX );

            try {
                Index indexItem = indices.stream()
                        .filter( index -> index.getName().equals( indexName ) )
                        .findFirst()
                        .get();

                if (indexItem != null) {
                    String indexType = (String) hit.getSource().get( LINKED_TYPE );
                    ISO8601DateFormat format = new ISO8601DateFormat();
                    Date lastIndexed = format.parse( (String) hit.getSource().get( INDEX_FIELD_LAST_INDEXED ) );

                    indexItem.setId( hit.getId() );
                    indexItem.setLongName( (String) hitSource.get( INDEX_FIELD_IPLUG_NAME ) );
                    // check if linked component / iPlug is connected
                    PlugDescription iPlugDetail = iPlugService.getIPlugDetail( (String) hitSource.get( "plugId" ) );
                    if (iPlugDetail != null) {
                        indexItem.setConnected( true );
                    }
                    indexItem.setHasLinkedComponent( true );

                    for (IndexType type : indexItem.getTypes()) {
                        if (type.getName().equals( indexType )) {
                            type.setId( (String) hitSource.get( INDEX_FIELD_INDEX_ID ) );
                            type.setHasLinkedComponent( true );
                            type.setLastIndexed( lastIndexed );
                            type.setActive( settingsService.isActive( type.getId() ) );
                        }
                    }
                }
            } catch (Exception ex) {
                // skip and continue
            }
        }
    }

    /**
     * 
     * @param state
     * @return
     */
    private IndexState mapIndexingState(Map<String, Object> state) {
        IndexState indexState = new IndexState();

        Integer numProcessed = (Integer) state.get( "numProcessed" );
        Integer totalDocs = (Integer) state.get( "totalDocs" );

        // indexState.setMessage( message );
        indexState.setRunning( state.get( "running" ).equals( true ) );
        if (numProcessed != null) {
            indexState.setNumProcessed( Integer.valueOf( numProcessed ) );
        }
        if (totalDocs != null) {
            indexState.setTotalDocs( Integer.valueOf( state.get( "totalDocs" ).toString() ) );
        }
        return indexState;
    }

    /**
     * 
     * @param date
     * @return
     */
    private Date mapDate(String date) {
        if (date != null) {
            return new DateTime( date ).toDate();
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
    // result.setSource( (String) hit.getSource().get( "dataSourceName" ) );
    // results.add( result );
    // }
    // return results;
    // }

    /**
     * 
     * @param query
     * @return
     */
    public SearchHits search(QueryBuilder query) {
        String[] indices = getActiveIndices();

        // when no index was selected then do not return any hits
        if (indices.length == 0) {
            return new SearchHits( new SearchHit[0], 0, 0 );
        }

        String[] justIndexNames = Stream.of( indices )
                .map( indexWithType -> indexWithType.split( ":" )[0] )
                .collect( Collectors.toSet() )
                .toArray( new String[0] );

        BoolQueryBuilder indexTypeFilter = queryBuilderService.createIndexTypeFilter( indices );

        // TODO: handle not existing index names which lead to an exception
        
        SearchResponse response = client.prepareSearch( justIndexNames )
                .setQuery( QueryBuilders.boolQuery().must( query ).must( indexTypeFilter ) )
                .setFetchSource( new String[] { "*" }, null )
                .setSize( 10 )
                .get();

        return response.getHits();
    }

    /**
     * 
     * @return
     */
    public String[] getActiveIndices() {
        List<String> result = new ArrayList<String>();

        // get active components
        Set<String> activeComponents = settingsService.getActiveComponentIds();
        
        if (activeComponents.size() == 0) {
            return new String[0];
        }

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        for (String active : activeComponents) {
            boolQuery.should( QueryBuilders.termQuery( INDEX_FIELD_INDEX_ID, active ) );
        }

        // get real index names from active components
        SearchResponse response = client.prepareSearch( INDEX_INFO_NAME )
                .setTypes( "info" )
                .setQuery( boolQuery )
                .setFetchSource( new String[] { LINKED_INDEX, LINKED_TYPE }, null )
                .setSize( 1000 )
                .get();

        // collect all referenced indices
        response.getHits().forEach( hit -> {
            String index = (String) hit.getSource().get( LINKED_INDEX );
            String type = (String) hit.getSource().get( LINKED_TYPE );
            if (index != null && type != null) {
                result.add(  index + ":" + type );
            }
        } );

        return result.toArray( new String[0] );
    }

    /**
     * 
     * @param indexId
     * @param hitId
     * @return
     */
    public IngridHitDetail getHitDetail(String indexId, String hitId) {
        GetResponse response = client.prepareGet( indexId, null, hitId )
                .setFetchSource( "*", null )
                .get();

        IngridHitDetail hit = mapHitDetail( response );
        return hit;
    }

    /**
     * 
     * @param hit
     * @return
     */
    private IngridHitDetail mapHitDetail(GetResponse hit) {
        Map<String, Object> source = hit.getSource();
        
        IngridHitDetail result = new IngridHitDetail(
                (String) source.get( "iPlugId" ),
                hit.getId(),
                -1,
                -1.0f,
                (String) source.get( "title" ),
                (String) source.get( "summary" ) );
        
        result.put( "source", source.get( "iPlugId" ) );
        result.put( "idf", source.get( "idf" ) );
        
        return result;
    }

    /**
     * 
     * @param id
     */
    public void deleteIndex(String id) {
        client.admin().indices().prepareDelete( id ).get();
    }

    public String getIPlugForIndex(String id) {
        SearchResponse response = client.prepareSearch( INDEX_INFO_NAME )
                .setTypes( "info" )
                .setQuery( QueryBuilders.termQuery( LINKED_INDEX, id ) )
                .setFetchSource( new String[] { "*" }, null )
                .setSize( 1000 )
                .get();
        
        SearchHit[] hits = response.getHits().getHits();
        
        // get first plugid found
        // it's possible that there are more than one documents returned since for each type a document exists 
        if (hits.length > 0) {
            return (String) hits[0].getSource().get( INDEX_FIELD_IPLUG_ID );
        } else {
            log.error( "There should be at least one corresponding component for the index: " + id );
        }
        return null;
    }
    
    private void prepareIndices() {
        boolean indexExists = indexManager.indexExists( INDEX_INFO_NAME );
        
        if (!indexExists) {
            InputStream defaultMappingStream = getClass().getClassLoader().getResourceAsStream( "ingrid-meta-mapping.json" );
            try {
                String mappingString = XMLSerializer.getContents( defaultMappingStream );
                indexManager.createIndex( INDEX_INFO_NAME, "info", mappingString );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }

}
