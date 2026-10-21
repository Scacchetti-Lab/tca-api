package com.api.tca.common.ai.dto.response.transcript.basic;

import com.api.tca.common.ai.dto.response.transcript.performance.PerformanceAnalyseDto;
import com.api.tca.common.ai.dto.response.transcript.strategic.StrategicAnalyseDto;

import java.util.Optional;

public record AnalyticsDto(
        Optional<StrategicAnalyseDto> strategic,
        PerformanceAnalyseDto performance
) {}
