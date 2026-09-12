package com.api.tca.common.ai.dto.response.transcript.performance;

import com.api.tca.common.ai.dto.response.transcript.basic.InsightsDto;

public record PerformanceAnalyseDto(
        PerformanceMetricsDto metrics,
        InsightsDto insights,
        String feedback
) {}
