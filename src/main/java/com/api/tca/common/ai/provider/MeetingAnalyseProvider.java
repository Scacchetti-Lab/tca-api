package com.api.tca.common.ai.provider;

import com.api.tca.common.ai.dto.request.MeetingEmbeddingDto;
import com.api.tca.common.ai.dto.request.predict.MeetingPredictDto;
import com.api.tca.common.ai.dto.response.PredictResponseDto;
import com.api.tca.common.ai.dto.response.ResponseModelDto;
import com.api.tca.common.exception.custom.FailOnPredictException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class MeetingAnalyseProvider {
    @Autowired
    private RestClient restClient;

    public void createEmbeds(UUID meetingId) {
        restClient.post()
                .uri("/v1/embed/meeting")
                .body(new MeetingEmbeddingDto(meetingId))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);
    }

    public ResponseModelDto<PredictResponseDto> predictFutureMeetings(UUID meetingId) {
        var data = restClient.post()
                .uri("/v1/predict/meeting")
                .body(new MeetingPredictDto(meetingId))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<ResponseModelDto<PredictResponseDto>>() {});

        if (data == null)
            throw new FailOnPredictException("Ocorreu no processamento da previsão");
        return data;
    }
}

