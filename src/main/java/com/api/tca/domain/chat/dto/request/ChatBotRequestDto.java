package com.api.tca.domain.chat.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record ChatBotRequestDto(
        @NotBlank(message = "Input é obrigatório")
        String input,
        @NotBlank(message = "Id da sessão é obriagtório")
        UUID sessionId
) {
}
