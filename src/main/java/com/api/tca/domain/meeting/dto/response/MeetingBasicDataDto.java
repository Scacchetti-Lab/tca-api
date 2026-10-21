package com.api.tca.domain.meeting.dto.response;

import com.api.tca.domain.meeting.entity.MeetingEntity;
import com.api.tca.domain.meeting.enums.MeetingPriority;
import com.api.tca.domain.meeting.enums.MeetingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record MeetingBasicDataDto(
        UUID id,
        String title,
        String summary,
        String clientName,
        LocalDateTime scheduled,
        MeetingStatus status,
        MeetingPriority priority
) {
    public MeetingBasicDataDto(MeetingEntity entity) {
        this(
            entity.getId(),
            entity.getTitle(),
            entity.getSummary(),
            entity.getClient() != null ? entity.getClient().getName() : entity.getTenant().getFantasyName(),
            entity.getScheduled(),
            entity.getStatus(),
            entity.getPriority()
        );
    }
}
