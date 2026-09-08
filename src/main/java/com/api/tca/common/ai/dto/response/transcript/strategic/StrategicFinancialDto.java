package com.api.tca.common.ai.dto.response.transcript.strategic;

import com.api.tca.domain.client.enums.FinancialImpact;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record StrategicFinancialDto(
        @JsonProperty("financial_impact")
        Double financialImpact,

        // valor monetário -- BigDecimal, não Double, para não perder precisão
        @JsonProperty("financial_impact_value")
        BigDecimal financialImpactValue,

        @JsonProperty("financial_impact_status")
        FinancialImpact financialImpactStatus
) {}