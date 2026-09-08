package com.api.tca.common.ai.dto.response.transcript.performance;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PerformanceMetricsDto(
        Double engagement,

        @JsonProperty("communication_quality")
        Double communicationQuality,

        @JsonProperty("opportunities_seized")
        Double opportunitiesSeized,

        @JsonProperty("objection_handling")
        Double objectionHandling,

        @JsonProperty("missed_opportunities")
        Double missedOpportunities,

        @JsonProperty("leads_converted")
        Integer leadsConverted
) {}
