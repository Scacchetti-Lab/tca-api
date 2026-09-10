package com.api.tca.domain.chat.dto.response;

import com.api.tca.domain.chat.enums.UserRoles;

import java.time.LocalDateTime;

public record MessageResponseDto(
        UserRoles role,
        String content,
        LocalDateTime createdOn
) {
}
