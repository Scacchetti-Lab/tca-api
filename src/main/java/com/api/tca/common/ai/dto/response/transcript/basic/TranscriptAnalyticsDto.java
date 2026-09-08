package com.api.tca.common.ai.dto.response.transcript.basic;

import com.api.tca.common.ai.dto.response.transcript.performance.PerformanceAnalyseDto;
import com.api.tca.common.ai.dto.response.transcript.strategic.StrategicAnalyseDto;

public record TranscriptAnalyticsDto(
        StrategicAnalyseDto strategic,
        PerformanceAnalyseDto performance
) {}
