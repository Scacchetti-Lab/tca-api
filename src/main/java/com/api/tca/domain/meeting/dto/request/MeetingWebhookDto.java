package com.api.tca.domain.meeting.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MeetingWebhookDto(
        @NotNull
        UUID jobId, // meetingId

        @NotBlank
        String transcript
) {
}
