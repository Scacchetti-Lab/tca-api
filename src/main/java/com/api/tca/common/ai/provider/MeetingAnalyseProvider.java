package com.api.tca.common.ai.provider;

import com.api.tca.common.ai.dto.request.MeetingEmbeddingDto;
import com.api.tca.common.ai.dto.response.AiDefaultResponseDto;
import com.api.tca.domain.meeting.exception.analyses.FailedOnEmbeddingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
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
}

