package com.api.tca.domain.client.dto.analyse;

import com.api.tca.domain.client.enums.FinancialImpact;

import java.math.BigDecimal;

public record ClientAnalysisDto(
        BigDecimal closingProbability,
        BigDecimal financialImpact,
        BigDecimal flexibility,
        FinancialImpact financialStatus,
        BigDecimal performance,
        BigDecimal risk
) { }
