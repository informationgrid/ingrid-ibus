package de.ingrid.ibus.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.elasticsearch.ElasticConfig;
import de.ingrid.elasticsearch.IndexInfo;
import de.ingrid.elasticsearch.IndexManager;
import de.ingrid.elasticsearch.search.IndexImpl;
import de.ingrid.ibus.comm.Bus;
import de.ingrid.ibus.comm.BusServer;
import de.ingrid.ibus.comm.registry.Registry;
import de.ingrid.utils.ElasticDocument;
import de.ingrid.utils.IPlug;
import de.ingrid.utils.IRecordLoader;
import de.ingrid.utils.IngridCall;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.query.IngridQuery;

@Service
public class SearchService implements IPlug, IRecordLoader, Serializable {
    
    private static Logger log = LogManager.getLogger( SearchService.class );
    
    private static final String CENTRAL_INDEX_ID = "__centralIndex__";

    /**
     * 
     */
    private static final long serialVersionUID = 7102378897547409841L;

    @Autowired
    private IndicesService indexService;
    
    @Autowired
    private IndexManager indexManager;

    @Autowired
    private IndexImpl indexUtils;
    
    @Autowired
    private ElasticConfig elasticConfig;
    
    @Autowired 
    private BusServer busServer;
    
    private String[] fields = new String[] { "metainfo", "t01_object.obj_id", "t02_address","t02_address.adr_id", "capabilities_url","parent","city","iPlugId","organisation","kml","refering","title","content","t011_obj_geo","t02_address4","t02_address3","t02_address5","children","datatype","provider","additional_html_1","street","y1","y2","t021_communication","t011_obj_serv","t02_address2","t022_adr_adr3","summary","t011_obj_serv_op_connpoint","zip","publish_id","t022_adr_adr","t03_catalogue","t012_obj_adr","idf","title2","title3","dataSourceName","t01_object","partner","refering_service_uuid","x1","boost","x2","parent4","parent5","object_reference","parent2","parent3","incl_meta","t01_object.org_obj_id", "t01_object.obj_class","metaclass" };
    private String[] datatypes = new String[] { "metadata", "dsc_ecs", "default", "topics", "dsc_ecs_address", "address", "IDF_1.0" };
    
    @PostConstruct
    public void init() {
        Registry registry = busServer.getRegistry();
        PlugDescription pd = new PlugDescription();
        pd.setProxyServiceURL( CENTRAL_INDEX_ID );
        pd.setIPlugClass( "igesearchplug" );
        pd.setRecordLoader( true );
        pd.setDataSourceDescription( "central index" );
        
        for (String datatype: datatypes) {
            pd.addDataType( datatype );
        }
        
        for (String field : fields) {
            pd.addField( field );
        }
        
        pd.put( "overrideProxy", this );
        registry.addPlugDescription( pd  );
        
        registry.activatePlug( CENTRAL_INDEX_ID );
        
        new SimulatedLifesign( registry, pd );
    }

    public IngridHits searchAndDetail(IngridQuery query, int hitsPerPage, int currentPage, int startHit, int maxMilliseconds, String[] requestedFields) {
        try {
            
            @SuppressWarnings("deprecation")
            IngridHits iPlugsResult = Bus.getInstance().searchAndDetail( query, 10, 0, 0, 30000, null );
            IngridHit[] iPlugHits = iPlugsResult.getHits();
            
            return new IngridHits( (int) iPlugsResult.length(), iPlugHits );
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }


        // IngridHits hits = null;
        // List<IngridHit> ingridHits = new ArrayList<IngridHit>();
        //
        // BoolQueryBuilder esQuery = queryConverter.convert( query );
        //
        //
        // SearchHits dHits = indexService.search( esQuery );
        //
        // dHits.forEach( hit -> {
        //
        // IngridHit ingridHit = new IngridHit();
        //
        //
        // String title = (String) hit.getSource().get( "title" );
        // String summary = (String) hit.getSource().get( "summary" );
        // ingridHit.put( "esIndex", hit.getIndex() );
        // ingridHit.put( "esType", hit.getType() );
        // ingridHit.put( "dataSourceName", hit.getSource().get( "dataSourceName" ) );
        // ingridHit.setDataSourceId( 0 );
        // ingridHit.setDocumentId( hit.getId() );
        // ingridHit.setPlugId( (String) hit.getSource().get( "iPlugId" ) );
        //
        //
        // ingridHit.setScore( hit.getScore() );
        // IngridHitDetail detail = new IngridHitDetail( ingridHit, title, summary );
        //
        // detail.setDocumentId( hit.getId() );
        //
        // // TODO: get class name from hit, which is used to display the detail in portal
        // // use different method, like the type how document should be displayed!?
        // detail.setIplugClassName( "igesearchplug" );
        //
        // // addPlugDescriptionInformations( detail, requestedFields );
        //
        // prepareDetail( detail, hit, requestedFields );
        //
        // ingridHit.setHitDetail( detail );
        // ingridHits.add( ingridHit );
        //
        // });
        //
        // hits = new IngridHits( (int)dHits.getTotalHits(), ingridHits.toArray( new IngridHit[0] ) );
        //
        // return hits;
    }
    
//    private void prepareDetail(IngridHitDetail detail, SearchHit dHit, String[] requestedFields) {
//
//        if (requestedFields != null) {
//            for (String field : requestedFields) {
//                if (dHit.getField( field ) != null) {
//                    if (dHit.getField( field ).getValue() instanceof String) {
//                        detail.put( field, new String[] { dHit.getField( field ).getValue() } );
//                    } else {
//                        detail.put( field, dHit.getField( field ).getValue() );
//                    }
//                }
//            }
//        }
//    }

    @Override
    public IngridHits search(IngridQuery query, int start, int length) throws Exception {
        
        elasticConfig.communicationProxyUrl = CENTRAL_INDEX_ID;
        elasticConfig.activeIndices = indexService.getActiveIndices();
        
        return indexUtils.search( query, start, length );
        
//        IngridHits hits = null;
//        List<IngridHit> ingridHits = new ArrayList<IngridHit>();
//        
//        BoolQueryBuilder esQuery = queryConverter.convert( query );
//        
//        
//        SearchHits dHits = indexService.search( esQuery );
//        
//        dHits.forEach( hit -> {
//            
//            IngridHit ingridHit = new IngridHit();
//            
//            
//            String title = (String) hit.getSource().get( "title" );
//            String summary = (String) hit.getSource().get( "summary" );
//            ingridHit.put( "esIndex", hit.getIndex() );
//            ingridHit.put( "esType", hit.getType() );
//            ingridHit.put( "dataSourceName", hit.getSource().get( "dataSourceName" ) );
//            ingridHit.setDataSourceId( 0 );
//            ingridHit.setDocumentId( hit.getId() );
//            // ingridHit.setPlugId( (String) hit.getSource().get( "iPlugId" ) );
//            ingridHit.setPlugId( CENTRAL_INDEX_ID );
//            
//            
//            ingridHit.setScore( hit.getScore() );
//            IngridHitDetail detail = new IngridHitDetail( ingridHit, title, summary );
//            
//            detail.setDocumentId( hit.getId() );
//            
//            // TODO: get class name from hit, which is used to display the detail in portal
//            // use different method, like the type how document should be displayed!?
//            detail.setIplugClassName( "igesearchplug" );
//            
//            ingridHit.setHitDetail( detail );
//            ingridHits.add( ingridHit );
//            
//        });
//        
//        hits = new IngridHits( (int)dHits.getTotalHits(), ingridHits.toArray( new IngridHit[0] ) );
//        
//        return hits;
    }

    @Override
    public IngridHitDetail getDetail(IngridHit hit, IngridQuery query, String[] requestedFields) throws Exception {
//        BoolQueryBuilder esQuery = queryConverter.convert( query );
//        
//        
//        String indexId = (String) hit.get("esIndex");
//        String hitId = hit.getDocumentId();
//        SearchResult result = indexService.getHitDetail( indexId, hitId );
//        
//        result.ge
//        
//        String title = (String) hit.getSource().get( "title" );
//        String summary = (String) hit.getSource().get( "summary" );
//        IngridHitDetail detail = new IngridHitDetail( hit, title, summary );
//        
//        detail.setDocumentId( hit.getId() );
//        
//        // TODO: get class name from hit, which is used to display the detail in portal
//        // use different method, like the type how document should be displayed!?
//        detail.setIplugClassName( "igesearchplug" );
//        
//        ingridHit.setHitDetail( detail );
        return indexUtils.getDetail( hit, query, requestedFields );
    }

    @Override
    public IngridHitDetail[] getDetails(IngridHit[] hits, IngridQuery query, String[] requestedFields) throws Exception {
//        IngridHitDetail[] details = new IngridHitDetail[hits.length];
//        int i = 0;
//        for (IngridHit hit : hits) {
//            details[i++] = getDetail(hit, query, requestedFields);
//        }
//        return details;
        
        return indexUtils.getDetails( hits, query, requestedFields );
    }

    @SuppressWarnings("unchecked")
    @Override
    public IngridDocument call(IngridCall targetInfo) throws Exception {
        IngridDocument doc = new IngridDocument();
        
        Map<String, Object> parameters = null;
        Object parameter = null;
        
        switch (targetInfo.getMethod()) {
        case "createIndex":
            parameter = targetInfo.getParameter();
            boolean success = indexManager.createIndex( (String) parameter );
            //if (!success) throw new RuntimeException( "Index could not be created: " + name );
            doc.put( "result", success );
            break;
            
        case "getIndexNameFromAliasName":
            parameters = (Map<String, Object>) targetInfo.getParameter();
            String aliasName = indexManager.getIndexNameFromAliasName(
                    (String) parameters.get( "indexAlias" ), 
                    (String) parameters.get( "partialName" ) );
            doc.put( "result", aliasName );
            break;
            
        case "switchAlias":
            parameters = (Map<String, Object>) targetInfo.getParameter();
            indexManager.switchAlias(
                    (String) parameters.get( "aliasName" ),
                    (String) parameters.get( "oldIndex" ),
                    (String) parameters.get( "newIndex" ) );
            break;
            
        case "checkAndCreateInformationIndex":
            indexManager.checkAndCreateInformationIndex();
            break;
            
        case "getIndexTypeIdentifier":
            parameter = targetInfo.getParameter();
            String resultIndexTypeIdent = indexManager.getIndexTypeIdentifier( (IndexInfo) parameter );
            doc.put( "result", resultIndexTypeIdent );
            break;
            
        case "addBasicFields":
            parameters = (Map<String, Object>) targetInfo.getParameter();
            indexManager.addBasicFields(
                    (ElasticDocument) parameters.get( "document" ),
                    (IndexInfo) parameters.get( "info" ) );
            break;
            
        case "update":
            parameters = (Map<String, Object>) targetInfo.getParameter();
            indexManager.update(
                    (IndexInfo) parameters.get( "indexinfo" ),
                    (ElasticDocument) parameters.get( "doc" ),
                    (boolean) parameters.get( "updateOldIndex" ) );
            break;
            
        case "updateIPlugInformation":
            parameters = (Map<String, Object>) targetInfo.getParameter();
            indexManager.updateIPlugInformation(
                    (String) parameters.get( "id" ),
                    (String) parameters.get( "info" ) );
            break;
            
        case "flush":
            indexManager.flush();
            break;
            
        case "deleteIndex":
            parameter = targetInfo.getParameter();
            indexManager.deleteIndex( (String) parameter );
            break;
            
        case "getMapping":
            parameter = targetInfo.getParameter();
            Map<String, Object> resultMapping = indexManager.getMapping( (IndexInfo) parameter );
            doc.put( "result", resultMapping );
            break;
            
        case "updateHearbeatInformation":
            parameter = targetInfo.getParameter();
            indexManager.updateHearbeatInformation( (List<String>) parameter );
            break;
            
        default:
            log.error( "Calling method not supported: " + targetInfo.getMethod() );
        }
        return doc;
    }

    @Override
    public void configure(PlugDescription plugDescription) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void close() throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Record getRecord(IngridHit hit) throws Exception {
        Record record = new Record();
        
        ElasticDocument doc = indexUtils.getDocById( hit.getDocumentId() );
        String data = (String) doc.get( "idf" );
        record.put( "data", data );
        record.put( "compressed", "false" );
        return record;
    }
}
