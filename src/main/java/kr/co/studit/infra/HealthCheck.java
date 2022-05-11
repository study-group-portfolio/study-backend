package kr.co.studit.infra;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheck {
    @GetMapping("/")
    public String heathCheck() {
        return "The service is up and running...";
    }
}