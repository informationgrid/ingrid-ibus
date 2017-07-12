package de.ingrid.ibus.web;

import java.util.List;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonView;

import de.ingrid.ibus.model.Index;
import de.ingrid.ibus.model.View;
import de.ingrid.ibus.service.IndicesService;

@CrossOrigin
@Controller
@RequestMapping("/api")
public class IndicesController {
    @Autowired
    private IndicesService indicesService;

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
    public ResponseEntity<Void> activateIndex(@PathVariable String id) {
        return ResponseEntity.status( HttpStatus.NOT_IMPLEMENTED ).build();
    }

    @PutMapping("/indices/{id}/deactivate")
    @ResponseBody
    public ResponseEntity<Void> deactivateIndex(@PathVariable String id) {
        return ResponseEntity.status( HttpStatus.NOT_IMPLEMENTED ).build();
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

}
