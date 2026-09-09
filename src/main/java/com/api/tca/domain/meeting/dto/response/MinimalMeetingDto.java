package com.api.tca.domain.meeting.dto.response;

import com.api.tca.domain.meeting.entity.MeetingEntity;
import com.api.tca.domain.meeting.enums.MeetingStatus;

import java.util.UUID;

public record MinimalMeetingDto(
        UUID id,
        String title,
        MeetingStatus status,
        String clientFantasyName
) {
    public MinimalMeetingDto(MeetingEntity dto) {
        this(
                dto.getId(),
                dto.getTitle(),
                dto.getStatus(),
                dto.getClient().getFantasyName()
        );
    }
}
