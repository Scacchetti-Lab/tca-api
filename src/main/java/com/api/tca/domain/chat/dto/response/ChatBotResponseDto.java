package com.api.tca.domain.chat.dto.response;

public record ChatBotResponseDto(
        String input,
        String output,
        String previousInteractId
) {
}
