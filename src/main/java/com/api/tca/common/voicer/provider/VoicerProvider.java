package com.api.tca.common.voicer.provider;

import com.api.tca.common.voicer.dto.request.ReadAudioDto;
import com.api.tca.common.voicer.dto.response.BaseResponseDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@Component
public class VoicerProvider {

    @Autowired
    @Qualifier("voicerRestClient")
    private RestClient restClient;

    public BaseResponseDto<?> transcriptAudio(MultipartFile audio) {
        ReadAudioDto dto = new ReadAudioDto(audio);

        var response = restClient.post()
                .uri("/voice/webhook/read-file")
                .body(dto)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(BaseResponseDto.class);

        if (!response.status().equals("102")) {
            log.debug(response.message());
            return transcriptAudio(audio);
        }

        return response;
    }
}
