package de.ingrid.ibus.service;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.admin.elasticsearch.converter.QueryConverter;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;

@Service
public class SearchService {
    
    @Autowired
    private IndicesService indexService;
    
    @Autowired
    private QueryConverter queryConverter;

    public IngridHits searchAndDetail(IngridQuery query, int hitsPerPage, int currentPage, int startHit, int maxMilliseconds, String[] requestedFields) {
        IngridHits hits = null;
        List<IngridHit> ingridHits = new ArrayList<IngridHit>();
        
        BoolQueryBuilder esQuery = queryConverter.convert( query );
        
        
        SearchHits dHits = indexService.search( esQuery );
        
        dHits.forEach( hit -> {
            
            IngridHit ingridHit = new IngridHit();
            
            
            String title = (String) hit.getSource().get( "title" );
            String summary = (String) hit.getSource().get( "summary" );
            ingridHit.put( "esIndex", hit.getIndex() );
            ingridHit.put( "esType", hit.getType() );
            ingridHit.put( "dataSourceName", hit.getSource().get( "dataSourceName" ) );
            ingridHit.setDataSourceId( 0 );
            ingridHit.setScore( hit.getScore() );
            IngridHitDetail detail = new IngridHitDetail( ingridHit, title, summary );
            
            detail.setDocumentId( hit.getId() );
            
            // addPlugDescriptionInformations( detail, requestedFields );
            
            prepareDetail( detail, hit, requestedFields );
            
            ingridHit.setHitDetail( detail );
            ingridHits.add( ingridHit );
            
        });
        
        hits = new IngridHits( (int)dHits.getTotalHits(), ingridHits.toArray( new IngridHit[0] ) );
        
        return hits;
    }
    
    private void prepareDetail(IngridHitDetail detail, SearchHit dHit, String[] requestedFields) {


        if (requestedFields != null) {
            for (String field : requestedFields) {
                if (dHit.getField( field ) != null) {
                    if (dHit.getField( field ).getValue() instanceof String) {
                        detail.put( field, new String[] { dHit.getField( field ).getValue() } );
                    } else {
                        detail.put( field, dHit.getField( field ).getValue() );
                    }
                }
            }
        }
    }
}
