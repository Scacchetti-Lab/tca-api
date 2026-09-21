package com.api.tca.common.ai.dto.request;

import com.api.tca.domain.user.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record MeetingTranscriptProcessDto(
        @JsonProperty("meeting_id")
        UUID meetingId,

        TranscriptProcessRequestDto transcriptRequest,

        UserEntity user
) { }
