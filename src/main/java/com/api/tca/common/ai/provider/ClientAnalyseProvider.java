package com.api.tca.common.ai.provider;

import com.api.tca.common.ai.dto.request.ClientEmbeddingDto;
import com.api.tca.common.ai.dto.request.predict.ClientPredictDto;
import com.api.tca.common.ai.dto.request.predict.MeetingPredictDto;
import com.api.tca.common.ai.dto.response.AiDefaultResponseDto;
import com.api.tca.common.ai.dto.response.PredictResponseDto;
import com.api.tca.common.ai.dto.response.ResponseModelDto;
import com.api.tca.common.exception.custom.FailOnPredictException;
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

    public ResponseModelDto<PredictResponseDto> predictClientStatus(UUID clientId) {
        var data = restClient.post()
                .uri("/v1/predict/client")
                .body(new ClientPredictDto(clientId))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<ResponseModelDto<PredictResponseDto>>() {});

        if (data == null)
            throw new FailOnPredictException("Ocorreu no processamento da previsão");
        return data;
    }
}
