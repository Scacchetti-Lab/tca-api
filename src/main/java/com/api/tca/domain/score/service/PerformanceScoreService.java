package com.api.tca.domain.score.service;

import com.api.tca.domain.meeting.entity.MeetingAnalysePerformanceEntity;
import com.api.tca.domain.meeting.service.MeetingService;
import com.api.tca.domain.score.dto.serviceParams.PerformancePointsDto;
import com.api.tca.domain.score.repository.PerformanceScoreRepository;
import com.api.tca.domain.user.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PerformanceScoreService {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private PerformanceScoreRepository scoreRepository;

    @Transactional
    public void setUserScore(UserEntity user, MeetingAnalysePerformanceEntity performance) {
        int baseScore = calculateBaseScore(new PerformancePointsDto(performance));
    }


    private Integer calculateBaseScore(PerformancePointsDto points) {
        return (int) Math.round((points.engagement() + points.communication() + points.objectionHandling() + points.opportunities()) - points.missedOpportunities());
    }

    private Double getMultiplier(UserEntity user) {

    }

}
