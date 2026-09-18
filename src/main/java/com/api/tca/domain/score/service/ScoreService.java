package com.api.tca.domain.score.service;

import com.api.tca.domain.client.entity.ClientEntity;
import com.api.tca.domain.meeting.entity.MeetingEntity;
import com.api.tca.domain.meeting.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ScoreService {

    @Autowired
    private PerformanceScoreService performanceService;

    @Autowired
    private StrategicScoreService strategicPerformance;

    public void saveScorePoints(MeetingService meetingService, MeetingEntity meeting, ClientEntity client) {
        //calculateStrategicBaseScore(client);
        calculatePerformanceBaseScore(meetingService, meeting);
    }


    private void calculateStrategicBaseScore(ClientEntity client) {
        Set<MeetingEntity> meetings = client.getMeetings();


    }

    private void calculatePerformanceBaseScore(MeetingService meetingService, MeetingEntity meetingToAnalyse) {
        performanceService.setScorePoints(meetingService, meetingToAnalyse);
    }
}
