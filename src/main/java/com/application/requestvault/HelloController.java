package com.application.requestvault;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HelloController {

    @GetMapping("/status")
    public Map<String, String> Status() {
        return Map.of(
                "application", "RequestVault",
                "status", "running"
        );
    }
}
