package com.api.tca.domain.healthcheck.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health-check")
public class HealtchCheckController {

    @GetMapping
    public ResponseEntity<?> getHealthCheck() {
        String message = "API Totvs Commercial AI (tca) - Processamento OK";

        return ResponseEntity.ok(message);
    }
}
