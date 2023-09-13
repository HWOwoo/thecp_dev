package com.thecp.dev.global.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // @Controller + @ResponseBody
@RequestMapping("/api")
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "Hello World!";
    }

}
