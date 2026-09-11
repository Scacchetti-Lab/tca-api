package com.api.tca.common.ai.dto.request.chat;


public record ChatBotProviderRequest(
        String message,
        String previousInteractId
) {
}
