package com.api.tca.domain.meeting.dto.request;

import com.api.tca.domain.meeting.enums.AudioTranscriptType;
import com.api.tca.domain.user.dto.auth.TokenRegisterDto;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MeetingLiveDto(
        @NotNull
        AudioTranscriptType type,

        UUID externalClientId,

        @NotNull
        TokenRegisterDto tokenData
) {
}
