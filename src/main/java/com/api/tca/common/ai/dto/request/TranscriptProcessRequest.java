package com.api.tca.common.ai.dto.request;

import com.api.tca.domain.user.enums.ProfileTypes;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;

import java.util.UUID;

public record TranscriptProcessRequest(
        @JsonProperty("transcript_id")
        UUID transcriptId,

        @Enumerated(EnumType.STRING)
        ProfileTypes profile,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        ClientContextRequestDto clientContext
) {
}
