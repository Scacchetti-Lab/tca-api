package com.api.tca.common.voicer.provider;

import com.api.tca.common.voicer.dto.request.ReadAudioDto;
import com.api.tca.common.voicer.dto.response.BaseResponseDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Log4j2
@Component
public class VoicerProvider {

    @Autowired
    @Qualifier("voicerRestClient")
    private RestClient restClient;

    public BaseResponseDto<?> transcriptAudio(MultipartFile audio) throws IOException {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("audio_name", audio.getOriginalFilename());
        body.add("audio", audio.getResource());

        var response = restClient.post()
                .uri("/voice/webhook/read-audio")
                .body(body)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .retrieve()
                .body(BaseResponseDto.class);

        if (!response.status().equals("102")) {
            log.debug(response.message());
            return response;
        }
        return response;
    }
}
