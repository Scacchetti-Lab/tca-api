package com.api.tca.common.ai.dto.request.predict;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record MeetingPredictDto(@JsonProperty("meeting_id") UUID meetingId) {
}
