package com.api.tca.domain.client.dto.analyse;

import com.api.tca.domain.client.entity.ClientAnalyseEntity;
import com.api.tca.domain.client.enums.FinancialImpact;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;

public record ClientAnalyseDto(
        double performance,
        double closingProbability,
        double flexibility,
        double risk,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        BigDecimal financialImpact,
        FinancialImpact financialImpactStatus
) {

    public ClientAnalyseDto(ClientAnalyseEntity analyse) {
        this(
                analyse.getPerformance(),
                analyse.getClosingProbability(),
                analyse.getFlexibility(),
                analyse.getRisk(),
                analyse.getFinancialImpact(),
                analyse.getFinancialStatus()
        );
    }
}
