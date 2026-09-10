package com.api.tca.domain.chat.dto.response;

import com.api.tca.domain.user.dto.user.MinimalUserDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ChatSessionDto(
    @NotBlank
    String title,

    @Valid
    MinimalUserDto user,

    @NotNull
    LocalDateTime lastActivity
) {
}
