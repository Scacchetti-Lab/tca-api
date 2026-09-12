package com.api.tca.common.ai.dto.response.transcript.basic;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TranscriptDescriptionDto(
        @JsonProperty("company_name")
        String companyName,

        @JsonProperty("company_representor")
        String companyRepresentor,

        String segment,

        List<StakeholderDto> stakeholders,

        @JsonProperty("meeting_summary")
        String meetingSummary
) {}
