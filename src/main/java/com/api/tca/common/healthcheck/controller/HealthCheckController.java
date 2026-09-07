package com.api.tca.common.healthcheck.controller;

import com.api.tca.common.healthcheck.dto.HealthCheckResponse;
import com.api.tca.common.healthcheck.dto.HealthCheckStatus;
import com.api.tca.common.ai.provider.HealthCheckProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthCheckController {

    @Autowired
    private HealthCheckProvider provider;

    @GetMapping
    public ResponseEntity<HealthCheckStatus> getHealthCheck() {
        HealthCheckStatus status = new HealthCheckStatus("OK", "API Totvs Commercial AI (tca)", "1.0.0");

        return ResponseEntity.ok(status);
    }

    @GetMapping("/ai")
    public ResponseEntity<HealthCheckStatus> getAiHealthCheck() {
        return ResponseEntity.ok(provider.healthCheck());
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllSystemsHealthCheck() {
        return ResponseEntity.ok(new HealthCheckResponse(
                getHealthCheck().getBody(),
                getAiHealthCheck().getBody()
        ));
    }
}
