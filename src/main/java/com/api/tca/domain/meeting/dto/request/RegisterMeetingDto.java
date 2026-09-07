package com.api.tca.domain.meeting.dto.request;

import com.api.tca.domain.meeting.enums.MeetingPriority;
import com.api.tca.domain.meeting.enums.MeetingSource;
import com.api.tca.domain.meeting.enums.MeetingStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RegisterMeetingDto(
    String totvsId, // opcional
    String title, // opcional
    String summary,

    @NotNull(message = "Data de agendamento é obrigatória")
    LocalDateTime scheduled,

    @NotNull(message = "Origem da reunião é obrigatório")
    @Enumerated(EnumType.STRING)
    MeetingSource source,

    @Enumerated(EnumType.STRING)
    MeetingStatus status,

    String clientName,
    String clientRepresent,

    String email,

    @Enumerated(EnumType.STRING)
    MeetingPriority priority
) {
}
