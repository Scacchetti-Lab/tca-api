package com.api.tca.domain.score.service;

import com.api.tca.domain.client.entity.ClientEntity;
import com.api.tca.domain.meeting.entity.MeetingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ScoreService {

    @Autowired
    private PerformanceScoreService performanceService;

    @Autowired
    private StrategicScoreService strategicPerformance;

    public void calculateStrategicBaseScore(ClientEntity client) {
        Set<MeetingEntity> meetings = client.getMeetings();


    }

    public void calculatePerformanceBaseScore(MeetingEntity meetingToAnalyse) {
        var sellers = meetingToAnalyse.getUsers()
                .stream().filter(u -> u.getFirstProfileName().contains("Salesperson")).toList();

        sellers.forEach(u -> {
            performanceService.setScorePoints(meetingToAnalyse);
        });
    }
}
