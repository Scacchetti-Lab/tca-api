package com.api.tca.common.ai.dto.response.transcript;

import com.api.tca.domain.meeting.enums.MeetingPriority;

public record InsightsDto(
        MeetingPriority priority,
        Integer rating,
        String tips
) {}
