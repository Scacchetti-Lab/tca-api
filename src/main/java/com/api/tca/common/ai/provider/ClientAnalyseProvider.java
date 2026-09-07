package com.api.tca.common.ai.provider;

import com.api.tca.common.ai.dto.request.ClientEmbeddingRequest;
import com.api.tca.common.ai.dto.response.AiDefaultResponseDto;
import com.api.tca.common.ai.dto.response.ResponseModelDto;
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

    public AiDefaultResponseDto<String> addClientEmbeds(UUID clientId) {
        AiDefaultResponseDto<String> response = restClient.post()
                .uri("/v1/embed/client")
                .body(new ClientEmbeddingRequest(clientId))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<AiDefaultResponseDto<String>>() {});

        System.out.println(response != null ? response.content() : "Sem conteúdo");
        return response;
    }
}
