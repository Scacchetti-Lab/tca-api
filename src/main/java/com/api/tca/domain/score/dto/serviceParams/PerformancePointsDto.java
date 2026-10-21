package com.api.tca.domain.score.dto.serviceParams;

import com.api.tca.domain.meeting.entity.MeetingAnalysePerformanceEntity;

public record PerformancePointsDto(
        Double engagement,
        Double communication,
        Double opportunities,
        Double objectionHandling,
        Double missedOpportunities
) {
    public PerformancePointsDto(MeetingAnalysePerformanceEntity entity) {
        this(
            entity.getEngagement(),
            entity.getCommunicationQuality(),
            entity.getOpportunitiesSeized(),
            entity.getObjectionHandling(),
            entity.getMissedOpportunities()
        );
    }
}
