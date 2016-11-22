package hu.bme.instagram.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/error")
public class ErrorController {
    @RequestMapping(method = RequestMethod.GET)
    public String getError() {
        return "error";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String postError() {
        return "error";
    }
}
