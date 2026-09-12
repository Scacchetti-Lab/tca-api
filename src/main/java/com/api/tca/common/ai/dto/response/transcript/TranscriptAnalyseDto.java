package com.api.tca.common.ai.dto.response.transcript;

import com.api.tca.common.ai.dto.response.transcript.basic.AnalyticsDto;
import com.api.tca.common.ai.dto.response.transcript.basic.TranscriptDescriptionDto;
import com.api.tca.domain.transcript.enums.TranscriptStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

public record TranscriptAnalyseDto(
        TranscriptStatus status,

        @JsonProperty("basic_description")
        TranscriptDescriptionDto basicDescription,

        AnalyticsDto analytic
) { }
