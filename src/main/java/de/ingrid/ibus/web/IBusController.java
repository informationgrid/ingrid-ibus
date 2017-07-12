package de.ingrid.ibus.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IBusController {
    
    @RequestMapping("/indices/**")
    public String forward() {
        return "forward:/";
    }
}
