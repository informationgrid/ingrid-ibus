package de.ingrid.ibus.web;

import java.util.List;

import de.ingrid.ibus.comm.Bus;
import de.ingrid.ibus.comm.debug.DebugQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JsonNode;

import de.ingrid.ibus.model.Index;
import de.ingrid.ibus.model.IndexTypeDetail;
import de.ingrid.ibus.model.View;
import de.ingrid.ibus.service.IPlugService;
import de.ingrid.ibus.service.IndicesService;
import de.ingrid.ibus.service.SearchService;
import de.ingrid.ibus.service.SettingsService;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/api")
public class IndicesController {
    
    private static Logger log = LogManager.getLogger(IndicesController.class);
    
    @Autowired
    private IndicesService indicesService;
    
    @Autowired
    private SearchService searchService;
    
    @Autowired
    private SettingsService settingsService;
    
    @Autowired
    private IPlugService iplugService;

    @JsonView(View.Summary.class)
    @GetMapping("/indices")
    @ResponseBody
    public ResponseEntity<List<Index>> getIndices() {
        List<Index> indices = this.indicesService.getElasticsearchInfo().getIndices();
        return ResponseEntity.ok( indices );
    }

    @GetMapping("/indices/{id}")
    @ResponseBody
    public ResponseEntity<IndexTypeDetail> getIndexDetail(@PathVariable String id, @RequestParam String type) {
        IndexTypeDetail index;
        try {
            index = this.indicesService.getIndexDetail( id, type );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( null );
        }
        return ResponseEntity.ok( index );
    }

    @PutMapping("/indices/activate")
    @ResponseBody
    public ResponseEntity<Void> activateIndex(@RequestBody JsonNode json) throws Exception {
        boolean success = this.settingsService.activateIndexType( json.get( "id" ).asText() );
        
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR ).build();
        }
    }

    @PutMapping("/indices/deactivate")
    @ResponseBody
    public ResponseEntity<Void> deactivateIndex(@RequestBody JsonNode json) throws Exception {
        boolean success = this.settingsService.deactivateIndexType( json.get( "id" ).asText() );
        
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR ).build();
        }
    }

    @PutMapping("/indices")
    @ResponseBody
    public ResponseEntity<Void> updateIndex(@RequestBody JsonNode json) {
        return ResponseEntity.status( HttpStatus.NOT_IMPLEMENTED ).build();
    }

    @PutMapping("/indices/index")
    @ResponseBody
    public ResponseEntity<Void> planIndex(@RequestBody JsonNode json) {
        
        String plugId = indicesService.getIPlugForIndex(json.get( "id" ).asText());
        boolean success = this.iplugService.index( plugId );
        
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/indices")
    @ResponseBody
    public ResponseEntity<Void> removeIndex(@RequestBody JsonNode json) {
        // TODO: remove from active indices
        // TODO: also have a job to clean up active indices in case they have been deleted somewhere else
        this.indicesService.deleteIndex(json.get( "id" ).asText());
        return ResponseEntity.ok().build();
        
    }
    
    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<IngridHits> search(@RequestParam String query) {
        try {
            IngridQuery iQuery = QueryStringParser.parse( query + " ranking:score" );

            // enable debugging of query
            DebugQuery debugQ = Bus.getInstance().getDebugInfo();
            debugQ.setActiveAndReset();

            IngridHits searchAndDetail = searchService.searchAndDetail( iQuery, 5, 0, 0, 1000, new String[] { "title" } );

            searchAndDetail.put("debug", debugQ.getEvents());

            return ResponseEntity.ok(searchAndDetail);
        } catch (Exception ex) {
            log.error(ex);
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/indices/detail")
    @ResponseBody
    public ResponseEntity<IngridHitDetail> getHitDetail(@RequestBody JsonNode json) {
        String indexId = json.get( "indexId" ).asText();
        String hitId = json.get( "hitId" ).asText();
        
        IngridHitDetail hitDetail = this.indicesService.getHitDetail(indexId, hitId);
        return ResponseEntity.ok( hitDetail );
    }

}
