package de.ingrid.ibus.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IBusController {
//    @Autowired
//    private HelloWorldService helloWorldService;

    @GetMapping("/hello")
    @ResponseBody
    public String helloWorld() {
        return "Hello"; //this.helloWorldService.getHelloMessage();
    }
}
