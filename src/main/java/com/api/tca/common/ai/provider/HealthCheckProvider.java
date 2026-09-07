package com.api.tca.common.ai.provider;

import com.api.tca.common.healthcheck.dto.HealthCheckStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class HealthCheckProvider {

    @Autowired
    private RestClient restClient;

    public HealthCheckStatus healthCheck() {
        return restClient.get().uri("/health").retrieve().body(HealthCheckStatus.class);
    }
}
