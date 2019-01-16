/*-
 * **************************************************-
 * ingrid-ibus-backend
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
package de.ingrid.ibus.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;

import de.ingrid.ibus.service.IPlugService;
import de.ingrid.ibus.service.SearchService;
import de.ingrid.ibus.service.SettingsService;
import de.ingrid.utils.PlugDescription;

@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/api")
public class IPlugsController {
    
    private static Logger log = LogManager.getLogger(IPlugsController.class);
    
    @Autowired
    private SearchService searchService;
    
    @Autowired
    private SettingsService settingsService;
    
    @Autowired
    private IPlugService iPlugService;

    @GetMapping("/iplugs")
    @ResponseBody
    public ResponseEntity<PlugDescription[]> getIPlugs() {
        PlugDescription[] indices = this.iPlugService.getConnectedIPlugs();
        return ResponseEntity.ok( indices );
    }

    @GetMapping("/iplugs/detail")
    @ResponseBody
    public ResponseEntity<PlugDescription> getIPlugDetail(@RequestBody JsonNode json) {
        PlugDescription index;
        try {
            index = this.iPlugService.getIPlugDetail( json.get( "id" ).asText() );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( null );
        }
        return ResponseEntity.ok( index );
    }

    @PutMapping("/iplugs/activate")
    @ResponseBody
    public ResponseEntity<Void> activateIPlug(@RequestBody JsonNode json) throws Exception {
        this.iPlugService.activate( json.get( "id" ).asText() );
        
        return ResponseEntity.ok().build();
    }

    @PutMapping("/iplugs/deactivate")
    @ResponseBody
    public ResponseEntity<Void> deactivateIPlug(@RequestBody JsonNode json) throws Exception {
        this.iPlugService.deactivate( json.get( "id" ).asText() );
        
        return ResponseEntity.ok().build();
    }

//    @PutMapping("/indices/{id}")
//    @ResponseBody
//    public ResponseEntity<Void> updateIndex(@PathVariable String id) {
//        return ResponseEntity.status( HttpStatus.NOT_IMPLEMENTED ).build();
//    }

//    @GetMapping("/search")
//    @ResponseBody
//    public ResponseEntity<IngridHits> search(@RequestParam String query) {
//        try {
//            IngridQuery iQuery = QueryStringParser.parse( query );
//            IngridHits searchAndDetail = searchService.searchAndDetail( iQuery, 5, 0, 0, 1000, new String[] { "title" } );
//            return ResponseEntity.ok(searchAndDetail);
//            
//            //List<SearchResult> result = this.indicesService.search(query);
//            //return ResponseEntity.ok(result);
//        } catch (Exception ex) {
//            log.error(ex);
//            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
    
//    @GetMapping("/indices/{indexId}/{hitId}")
//    @ResponseBody
//    public ResponseEntity<IngridHitDetail> getHitDetail(@PathVariable String indexId, @PathVariable String hitId) {
//        IngridHitDetail hitDetail = this.indicesService.getHitDetail(indexId, hitId);
//        return ResponseEntity.ok( hitDetail );
//    }

}
