package org.example.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    @PostMapping("/api/demo")
    public String demo() {
        throw new IllegalArgumentException("Controller error.");
    }

}
