package com.api.tca.domain.score.dto;

import com.api.tca.domain.score.enums.FinancialImpactLevel;

import java.math.BigDecimal;

public record FinancialStrategicScore(
        BigDecimal percentage,
        BigDecimal financialImpact,
        FinancialImpactLevel level
) {
}
