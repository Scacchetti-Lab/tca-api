package com.api.tca.domain.chat.dto.response;

import com.api.tca.common.helpers.UserRoleHelper;
import com.api.tca.domain.chat.entity.AiSessionEntity;
import com.api.tca.domain.chat.enums.AiModel;
import com.api.tca.domain.user.dto.user.MinimalUserDto;
import com.api.tca.domain.user.entity.UserEntity;

import java.util.UUID;

public record ChatSessionWithOutputDto(
        UUID id,
        String title,
        MinimalUserDto user,
        AiModel modelUser,
        String userInput,
        String aiOutput
) {

    public ChatSessionWithOutputDto(AiSessionEntity session, UserEntity user, String userInput, String aiOutput) {
        this(
                session.getId(),
                session.getTitle(),
                new MinimalUserDto(
                        user.getFullName(),
                        user.getUsername(),
                        user.getAiTokenUsed(),
                        UserRoleHelper.normalize(user.getFirstProfileName())
                ),
                session.getModelUsed(),
                userInput,
                aiOutput
        );
    }
}
