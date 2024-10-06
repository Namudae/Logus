package com.logus.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // view를 리턴하겠다
public class SecurityController {

    // localhost:8080
    @GetMapping({"/"})
    public String index() {
        // 타임리프 기본폴더 src/main/resources/templates/
        return "index"; // src/main/resources/templates/index.html
    }
}
