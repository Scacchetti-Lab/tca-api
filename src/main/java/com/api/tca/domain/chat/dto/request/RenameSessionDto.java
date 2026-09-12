package com.api.tca.domain.chat.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RenameSessionDto(
        @NotBlank(message = "Título não pode ser nulo")
        String title
) {
}
