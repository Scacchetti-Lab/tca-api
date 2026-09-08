package com.api.tca.common.ai.provider;

import com.api.tca.common.ai.dto.request.TranscriptEmbedingRequest;
import com.api.tca.common.ai.dto.request.TranscriptProcessRequest;
import com.api.tca.common.ai.dto.response.AiDefaultResponseDto;
import com.api.tca.common.ai.dto.response.ResponseModelDto;
import com.api.tca.common.ai.dto.response.transcript.TranscriptAnalyseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class TranscriptProvider {

    @Autowired
    private RestClient restClient;


    public ResponseModelDto<TranscriptAnalyseDto> processTranscript(TranscriptProcessRequest request) {
        var response = restClient.post()
                .uri("/v1/transcript/process")
                .body(request)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<AiDefaultResponseDto<ResponseModelDto<TranscriptAnalyseDto>>>() {});

        if (response == null)
            return new ResponseModelDto<>(HttpStatus.BAD_REQUEST.value(), null,
                    "Falha ao gerar analise de transcricao para essa reunião", false);
        return response.content();
    }

    public ResponseModelDto<?> generateEmbedChunks(TranscriptEmbedingRequest request) {
        var response = restClient.post()
                .uri("/v1/embed/chunk")
                .body(request)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<AiDefaultResponseDto<ResponseModelDto<String>>>() {});

        if (response == null)
            return new ResponseModelDto<>(HttpStatus.BAD_REQUEST.value(), null, "Falha ao gerar chunks e embeds", false);
        return response.content();
    }
}
