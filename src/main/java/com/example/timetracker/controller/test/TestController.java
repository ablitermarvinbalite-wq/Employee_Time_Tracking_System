package com.example.timetracker.controller.test;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping
    public String test() {
        return "App is running";
    }
}