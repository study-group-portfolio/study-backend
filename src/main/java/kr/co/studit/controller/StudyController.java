package kr.co.studit.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudyController {
    @GetMapping("/")
    public String index(){

        return "hello";
    }
}
