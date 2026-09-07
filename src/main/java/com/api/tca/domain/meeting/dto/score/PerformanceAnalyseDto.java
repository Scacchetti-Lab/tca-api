package com.api.tca.domain.meeting.dto.score;

import com.api.tca.domain.meeting.entity.MeetingAnalysePerformanceEntity;

public record PerformanceAnalyseDto(
        Double engagement,
        Double communicationQuality,
        Double opportunitiesSeized,
        Double objectionHandling,
        Double missedOpportunities,
        Integer leadsConverted,
        String tips,
        String feedback
) {
    public PerformanceAnalyseDto(MeetingAnalysePerformanceEntity analyse) {
        this(
            analyse.getEngagement(),
            analyse.getCommunicationQuality(),
            analyse.getOpportunitiesSeized(),
            analyse.getObjectionHandling(),
            analyse.getMissedOpportunities(),
            analyse.getLeadsConverted(),
            analyse.getTips(),
            analyse.getFeedback()
        );
    }
}
