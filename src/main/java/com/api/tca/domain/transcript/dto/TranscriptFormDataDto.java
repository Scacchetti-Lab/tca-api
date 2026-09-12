package com.api.tca.domain.transcript.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record TranscriptFormDataDto(
        @JsonProperty("raw_transcript")
        @NotBlank
        String rawTranscript
) {
}
