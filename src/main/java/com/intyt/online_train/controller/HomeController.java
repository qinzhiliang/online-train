package com.intyt.online_train.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@CrossOrigin(origins = {"http://10.18.43.3:8000", "http://127.0.0.1:8000"}, allowCredentials = "true", maxAge = 3600)
@Controller
public class HomeController {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
        return "forward:login.html";
    }
}
