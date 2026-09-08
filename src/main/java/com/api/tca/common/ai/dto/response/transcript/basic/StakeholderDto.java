package com.api.tca.common.ai.dto.response.transcript.basic;

import com.api.tca.domain.meeting.enums.MeetingStakeholderSide;

public record StakeholderDto(
        String name,
        String role,
        MeetingStakeholderSide side
) {}
