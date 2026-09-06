package com.api.tca.domain.squad.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterSquadDto(
        @NotBlank
        String name,
        @NotBlank
        String code
) {
}
