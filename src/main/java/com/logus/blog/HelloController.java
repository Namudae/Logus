package com.logus.blog;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("hello")
    public String hello(Model model) { //model로 클라이언트에 값을 넘김
        model.addAttribute("data", "hello!!!");
        return "hello"; //hello.html 자동으로 붙음
    }
}
