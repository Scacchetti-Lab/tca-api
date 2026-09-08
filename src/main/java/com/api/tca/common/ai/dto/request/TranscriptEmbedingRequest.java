package com.api.tca.common.ai.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record TranscriptEmbedingRequest(@JsonProperty("transcript_id") UUID meetingId) {
}
