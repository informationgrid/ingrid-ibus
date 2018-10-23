/*-
 * **************************************************-
 * ingrid-ibus-backend
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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

import com.fasterxml.jackson.databind.JsonNode;
import de.ingrid.ibus.model.IPlugInfo;
import de.ingrid.ibus.service.IPlugService;
import de.ingrid.utils.PlugDescription;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/api")
public class IPlugsController {

    private static Logger log = LogManager.getLogger(IPlugsController.class);

    private final IPlugService iPlugService;

    @Autowired
    public IPlugsController(IPlugService iPlugService) {
        this.iPlugService = iPlugService;
    }

    @GetMapping("/iplugs")
    @ResponseBody
    public ResponseEntity<IPlugInfo[]> getIPlugs() {
        PlugDescription[] indices = this.iPlugService.getConnectedIPlugs();
        return ResponseEntity.ok(mapFromPlugdescription(indices));
    }

    private IPlugInfo[] mapFromPlugdescription(PlugDescription[] indices) {
        List<IPlugInfo> infos = new ArrayList<>();

        for (PlugDescription pd : indices) {
            if (!"__managementIPlug__".equals(pd.getProxyServiceURL()) && !"__centralIndex__".equals(pd.getProxyServiceURL())) {
                IPlugInfo iPlugInfo = new IPlugInfo();
                iPlugInfo.setActive((Boolean) pd.get("activated"));
                iPlugInfo.setId(pd.getProxyServiceURL());
                iPlugInfo.setName(pd.getDataSourceName());
                iPlugInfo.setDescription(pd.getDataSourceDescription());
                iPlugInfo.setAdminUrl(pd.getIplugAdminGuiUrl());
                iPlugInfo.setUseCentralIndex((Boolean) pd.getOrDefault("useRemoteElasticsearch", false));
                infos.add(iPlugInfo);
            }
        }

        return infos.toArray(new IPlugInfo[0]);
    }

    @GetMapping("/iplugs/detail")
    @ResponseBody
    public ResponseEntity<PlugDescription> getIPlugDetail(@RequestBody JsonNode json) {
        PlugDescription index;
        try {
            index = this.iPlugService.getIPlugDetail(json.get("id").asText());
        } catch (Exception e) {
            log.error("Error getting iPlug Detail", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(index);
    }

    @PutMapping("/iplugs/activate")
    @ResponseBody
    public ResponseEntity<Void> activateIPlug(@RequestBody JsonNode json) {
        this.iPlugService.activate(json.get("id").asText());

        return ResponseEntity.ok().build();
    }

    @PutMapping("/iplugs/deactivate")
    @ResponseBody
    public ResponseEntity<Void> deactivateIPlug(@RequestBody JsonNode json) {
        this.iPlugService.deactivate(json.get("id").asText());

        return ResponseEntity.ok().build();
    }
}
