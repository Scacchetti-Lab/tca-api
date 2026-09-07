package com.api.tca.domain.meeting.dto.request;

import com.api.tca.domain.meeting.enums.MeetingPriority;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDateTime;

public record UpdateMeetingDto(
        String title,
        String summary,
        LocalDateTime scheduled,

        @Enumerated(EnumType.STRING)
        MeetingPriority priority,

        String clientRepresent
) {
}
