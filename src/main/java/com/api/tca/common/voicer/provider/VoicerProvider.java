package com.api.tca.common.voicer.provider;

import com.api.tca.common.voicer.dto.request.ReadAudioDto;
import com.api.tca.common.voicer.dto.response.BaseResponseDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Log4j2
@Component
public class VoicerProvider {

    @Autowired
    @Qualifier("voicerRestClient")
    private RestClient restClient;

    public BaseResponseDto<?> transcriptAudio(MultipartFile audio, UUID jobId, String callbackUrl) throws IOException {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("audio", audio.getResource());
        body.add("callback_url", callbackUrl);
        body.add("job_id", jobId.toString());

        var response = restClient.post()
                .uri("/voice/meeting/transcript")
                .body(body)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .retrieve()
                .body(BaseResponseDto.class);

        if (response == null) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno durante o processamento");
        }

        if (!"202".equals(response.status())) {
            log.debug(response.message());
        }
        return response;
    }
}
