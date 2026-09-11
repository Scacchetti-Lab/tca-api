package com.api.tca.common.ai.provider;

import com.api.tca.common.ai.dto.request.chat.ChatBotProviderRequest;
import com.api.tca.common.ai.dto.response.ResponseModelDto;
import com.api.tca.common.ai.dto.response.chat.ChatBotFirstResponse;
import com.api.tca.common.ai.dto.response.chat.ChatBotProviderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ChatBotProvider {

    @Autowired
    private RestClient restClient;

    public ResponseModelDto<ChatBotFirstResponse> startChat(String input) {
        ChatBotProviderRequest request = new ChatBotProviderRequest(input, null);
        return restClient.post()
                .uri("/v1/chat/messages")
                .body(request)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<ResponseModelDto<ChatBotFirstResponse>>() {});

    }

    public ResponseModelDto<ChatBotProviderResponse> sendMessage(ChatBotProviderRequest request) {
        return restClient.post()
                .uri("/v1/chat/messages")
                .body(request)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<ResponseModelDto<ChatBotProviderResponse>>() {});
    }
}
