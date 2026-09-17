package com.api.tca.domain.chat.dto.response;

import com.api.tca.domain.chat.entity.AiSessionEntity;
import com.api.tca.domain.user.dto.user.MinimalUserDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChatSessionDto(
    UUID id,

    @NotBlank
    String title,

    @Valid
    MinimalUserDto user,

    @NotNull
    LocalDateTime lastActivity
) {

    public ChatSessionDto(AiSessionEntity entity) {
        this(
                entity.getId(),
                entity.getTitle(),
                new MinimalUserDto(entity.getUser()),
                entity.getLastActivity()
        );
    }
}
