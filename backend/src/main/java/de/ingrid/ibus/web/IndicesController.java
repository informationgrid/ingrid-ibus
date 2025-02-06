/*-
 * **************************************************-
 * ingrid-ibus-backend
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
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
package de.ingrid.ibus.web;

import java.util.List;

import de.ingrid.elasticsearch.ElasticConfig;
import de.ingrid.ibus.comm.Bus;
import de.ingrid.ibus.comm.debug.DebugQuery;
import de.ingrid.utils.queryparser.ParseException;
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

    @Autowired
    private ElasticConfig elasticConfig;

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
            index = this.indicesService.getIndexDetail( id );
        } catch (Exception e) {
            log.error("Error getting index detail", e);
            return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( null );
        }
        return ResponseEntity.ok( index );
    }

    @PutMapping("/indices/activate")
    @ResponseBody
    public ResponseEntity<Void> activateIndex(@RequestBody JsonNode json) throws Exception {
        String id = json.get("id").asText();
        boolean success = this.settingsService.activateIndexType(id);

        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR ).build();
        }
    }

    @PutMapping("/indices/deactivate")
    @ResponseBody
    public ResponseEntity<Void> deactivateIndex(@RequestBody JsonNode json) throws Exception {
        String id = json.get("id").asText();
        boolean success = this.settingsService.deactivateIndexType(id);

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
    public ResponseEntity<IngridHits> search(@RequestParam String query, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int hitsPerPage) throws ParseException {

        IngridQuery iQuery = QueryStringParser.parse( query );

        // for convenience we add ranking:score mainly needed to get any results
        if (!query.contains("ranking:")) {
            iQuery.put( IngridQuery.RANKED, IngridQuery.SCORE_RANKED );
        }

        // enable debugging of query
        DebugQuery debugQ = Bus.getInstance().getDebugInfo();
        debugQ.setActiveAndReset();

        String[] requestedFields = new String[] {elasticConfig.indexFieldTitle, elasticConfig.indexFieldSummary};
        IngridHits searchAndDetail = searchService.searchAndDetail( iQuery, hitsPerPage, page, page*hitsPerPage, 1000, requestedFields);

        if (searchAndDetail != null) {
            searchAndDetail.put("debug", debugQ.getEvents());
            return ResponseEntity.ok(searchAndDetail);
        } else {
            throw new RuntimeException("Search error! Please check the log file from the iBus.");
        }

    }

    @GetMapping("/indices/{indexId}/{docId}")
    @ResponseBody
    public ResponseEntity<IngridHitDetail> getHitDetail(@PathVariable(name = "indexId") String indexId, @PathVariable(name = "docId") String docId) {

        IngridHitDetail hitDetail = this.indicesService.getHitDetail(indexId, docId);
        return ResponseEntity.ok( hitDetail );
    }

}
