package de.ingrid.ibus.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LogoutController {
//    @Autowired
//    private HelloWorldService helloWorldService;

    /**
     * Demonstrates that invoking {@link HttpServletRequest#logout()} will log the user out.
     * We then redirect the user to the home page.
     */
    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return "redirect:/";
    }
}
