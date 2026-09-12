package com.api.tca.common.ai.provider;

import com.api.tca.common.ai.dto.request.ClientEmbeddingDto;
import com.api.tca.common.ai.dto.response.AiDefaultResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class ClientAnalyseProvider {
    @Autowired
    private RestClient restClient;

    public void addClientEmbeds(UUID clientId) {
        restClient.post()
                .uri("/v1/embed/client")
                .body(new ClientEmbeddingDto(clientId))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);
    }
}
