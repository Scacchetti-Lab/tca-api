package com.api.tca.common.ai.dto.response.transcript.strategic;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StrategicMetricsDto(
        @JsonProperty("company_performance")
        Double companyPerformance,

        @JsonProperty("closing_probability")
        Double closingProbability,

        Double flexibility,
        Double risk
) {}
