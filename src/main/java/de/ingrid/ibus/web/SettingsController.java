package de.ingrid.ibus.web;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.ingrid.ibus.service.SettingsService;

@CrossOrigin
@Controller
@RequestMapping("/api/settings")
public class SettingsController {
    
    private static Logger log = LogManager.getLogger(SettingsController.class);
    
    @Autowired
    private SettingsService settingsService;

    @GetMapping("/activeComponentIds")
    @ResponseBody
    public ResponseEntity<String[]> getActiveComponentIds(@RequestParam(defaultValue = "false") boolean verify) {
        // TODO: add parameter to verify if id exists
        log.info( "parameter verify was set to: " + verify );
        
        Set<String> ids = this.settingsService.getActiveComponentIds();
        return ResponseEntity.ok( ids.toArray( new String[0] ) );
    }

}
