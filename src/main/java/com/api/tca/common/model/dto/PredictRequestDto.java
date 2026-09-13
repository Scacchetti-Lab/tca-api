package com.api.tca.common.model.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PredictRequestDto(
        @NotNull(message = "Id é obrigatório")
        UUID entityId,
        Boolean reprocess
) {
}
