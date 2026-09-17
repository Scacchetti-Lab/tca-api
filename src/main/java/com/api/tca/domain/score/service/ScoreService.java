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

    public void calculateBaseScore(ClientEntity client) {
        Set<MeetingEntity> meetings = client.getMeetings();

        meetings.forEach(m -> {
            performanceService.calculateBaseScore(m.getMeetingAnalysePerformance());
            strategicPerformance.calculateBaseScore(m.getMeetingAnalyseStrategic());
        });
    }
}
