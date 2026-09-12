package com.api.tca.domain.user.dto.user;

import com.api.tca.common.helpers.UserRoleHelper;
import com.api.tca.domain.chat.enums.UserRoles;
import com.api.tca.domain.user.entity.UserEntity;

public record MinimalUserDto(
    String fullName,
    String userName,
    Integer aiTokenUsed,
    UserRoles role
) {

    public MinimalUserDto(UserEntity user) {
        this(
                user.getFullName(),
                user.getUsername(),
                user.getAiTokenUsed(),
                UserRoleHelper.normalize(user.getFirstProfileName())
        );
    }
}
