package com.api.tca.domain.meeting.dto.response;

import com.api.tca.domain.meeting.entity.MeetingStakeholdersEntity;
import com.api.tca.domain.meeting.enums.MeetingStakeholderSide;

public record MeetingStakeHolder(
        String name,
        String role,
        MeetingStakeholderSide side
) {

    public MeetingStakeHolder(MeetingStakeholdersEntity entity) {
        this(
                entity.getName(),
                entity.getRole(),
                entity.getSide()
        );
    }
}
