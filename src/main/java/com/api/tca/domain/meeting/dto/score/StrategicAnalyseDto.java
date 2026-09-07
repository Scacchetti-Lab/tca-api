package com.api.tca.domain.meeting.dto.score;

import com.api.tca.domain.client.enums.ClientStatus;
import com.api.tca.domain.meeting.entity.MeetingAnalyseStrategicEntity;
import com.api.tca.domain.meeting.enums.MeetingFinancialImpact;

import java.math.BigDecimal;

public record StrategicAnalyseDto(
        ClientStatus status,
        Double companyPerformance,
        Double closingProbability,
        Double flexibility,
        Double risk,
        Double financialImpactGrade,
        BigDecimal financialImpact,
        MeetingFinancialImpact financialImpactStatus,
        String feedback
) {
    public StrategicAnalyseDto(MeetingAnalyseStrategicEntity analyse) {
        this(
                analyse.getStatus(),
                analyse.getCompanyPerformance(),
                analyse.getClosingProbability(),
                analyse.getFlexibility(),
                analyse.getRisk(),
                analyse.getFinancialImpactGrade(),
                analyse.getFinancialImpact(),
                analyse.getFinancialImpactStatus(),
                analyse.getFeedback()
        );
    }
}
