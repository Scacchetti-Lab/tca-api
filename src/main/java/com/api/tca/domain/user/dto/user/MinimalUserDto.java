package com.api.tca.domain.user.dto.user;

import com.api.tca.domain.chat.enums.UserRoles;

public record MinimalUserDto(
    String fullName,
    String userName,
    Integer aiTokenUsed,
    UserRoles role
) {
}
