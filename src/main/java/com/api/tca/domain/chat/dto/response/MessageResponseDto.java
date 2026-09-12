package com.api.tca.domain.chat.dto.response;

import com.api.tca.domain.chat.entity.AiMessagesEntity;
import com.api.tca.domain.chat.enums.UserRoles;

import java.time.LocalDateTime;

public record MessageResponseDto(
        UserRoles role,
        String content,
        LocalDateTime createdOn
) {

    public MessageResponseDto(AiMessagesEntity entity) {
        this(
          entity.getRole(),
          entity.getContent(),
          entity.getCreatedOn()
        );
    }
}
