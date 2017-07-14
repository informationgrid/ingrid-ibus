package de.ingrid.ibus.web;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonView;

import de.ingrid.ibus.model.Index;
import de.ingrid.ibus.model.SearchResult;
import de.ingrid.ibus.model.View;
import de.ingrid.ibus.service.IndicesService;
import de.ingrid.ibus.service.SettingsService;

@CrossOrigin
@Controller
@RequestMapping("/api")
public class IndicesController {
    
    private static Logger log = LogManager.getLogger(IndicesController.class);
    
    @Autowired
    private IndicesService indicesService;
    
    @Autowired
    private SettingsService settingsService;

    @JsonView(View.Summary.class)
    @GetMapping("/indices")
    @ResponseBody
    public ResponseEntity<List<Index>> getIndices() {
        List<Index> indices = this.indicesService.getElasticsearchInfo().getIndices();
        return ResponseEntity.ok( indices );
    }

    @GetMapping("/indices/{id}")
    @ResponseBody
    public ResponseEntity<Index> getIndexDetail(@PathVariable String id) {
        Index index;
        try {
            index = this.indicesService.getIndexDetail( id );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( null );
        }
        return ResponseEntity.ok( index );
    }

    @PutMapping("/indices/{id}/activate")
    @ResponseBody
    public ResponseEntity<Void> activateIndex(@PathVariable String id) throws Exception {
        boolean success = this.settingsService.activateIndex( id );
        
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR ).build();
        }
    }

    @PutMapping("/indices/{id}/deactivate")
    @ResponseBody
    public ResponseEntity<Void> deactivateIndex(@PathVariable String id) throws Exception {
        boolean success = this.settingsService.deactivateIndex( id );
        
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR ).build();
        }
    }

    @PutMapping("/indices/{id}")
    @ResponseBody
    public ResponseEntity<Void> updateIndex(@PathVariable String id) {
        return ResponseEntity.status( HttpStatus.NOT_IMPLEMENTED ).build();
    }

    @PutMapping("/indices/{id}/index")
    @ResponseBody
    public ResponseEntity<Void> planIndex(@PathVariable String id) {
        return ResponseEntity.status( HttpStatus.NOT_IMPLEMENTED ).build();
    }

    @DeleteMapping("/indices/{id}")
    @ResponseBody
    public ResponseEntity<Void> removeIndex(@PathVariable String id) {
        return ResponseEntity.status( HttpStatus.NOT_IMPLEMENTED ).build();
    }
    
    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<List<SearchResult>> search(@RequestParam String query) {
        try {
            List<SearchResult> result = this.indicesService.search(query);
            return ResponseEntity.ok(result);
        } catch (Exception ex) {
            log.error(ex);
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
