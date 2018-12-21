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
import de.ingrid.ibus.service.ConfigurationService;
import de.ingrid.ibus.service.SettingsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@CrossOrigin
@Controller
@RequestMapping("/api")
public class SettingsController {
    
    private static Logger log = LogManager.getLogger(SettingsController.class);
    
    @Autowired
    private SettingsService settingsService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/settings/activeComponentIds")
    @ResponseBody
    public ResponseEntity<String[]> getActiveComponentIds(@RequestParam(defaultValue = "false") boolean verify) {
        // TODO: add parameter to verify if id exists
        log.info( "parameter verify was set to: " + verify );
        
        Set<String> ids = this.settingsService.getActiveComponentIds();
        return ResponseEntity.ok( ids.toArray( new String[0] ) );
    }

    @GetMapping("/settings")
    @ResponseBody
    public ResponseEntity<Properties> getConfiguration() {
        Properties configuration = this.configurationService.getConfiguration();
        return ResponseEntity.ok(configuration);
    }

    @PostMapping("/settings")
    @ResponseBody
    public ResponseEntity<Properties> writeConfiguration(@RequestBody JsonNode json) throws Exception {
        Properties configuration = this.configurationService.getConfiguration();

        mergeConfiguration(configuration, json);

        this.configurationService.writeConfiguration(configuration);
        return ResponseEntity.ok(configuration);
    }

    private void mergeConfiguration(Properties configuration, JsonNode json) {
        Iterator<Map.Entry<String, JsonNode>> fields = json.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> next = fields.next();
            String key = next.getKey();

            if (key.equals("spring.security.user.password")) {
                String password = next.getValue().asText();
                if (!password.isEmpty()) {
                    configuration.put(key, passwordEncoder.encode(password));
                    configuration.put("needPasswordChange", "false");
                }
            } else if (key.equals("needPasswordChange")) {
                // skip
            } else {
                configuration.put(key, next.getValue().asText());
            }
        }
    }

    @GetMapping("/status")
    @ResponseBody
    public ResponseEntity<?> getStatus() {
        Properties configuration = this.configurationService.getStatus();
        return ResponseEntity.ok(configuration);
    }
}
