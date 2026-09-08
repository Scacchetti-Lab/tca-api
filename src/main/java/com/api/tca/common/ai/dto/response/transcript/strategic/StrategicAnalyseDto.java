package com.api.tca.common.ai.dto.response.transcript.strategic;

import com.api.tca.common.ai.dto.response.transcript.basic.InsightsDto;
import com.api.tca.domain.client.enums.ClientStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

public record StrategicAnalyseDto(
        StrategicMetricsDto metrics,
        StrategicFinancialDto financial,

        @JsonProperty("company_status")
        ClientStatus companyStatus,

        InsightsDto insights,
        String feedback
) {}
