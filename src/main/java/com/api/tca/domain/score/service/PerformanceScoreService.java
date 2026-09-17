package com.api.tca.domain.score.service;

import com.api.tca.domain.meeting.entity.MeetingAnalysePerformanceEntity;
import com.api.tca.domain.meeting.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PerformanceScoreService {

    @Autowired
    private MeetingService meetingService;

    public void calculateBaseScore(MeetingAnalysePerformanceEntity strategic) {

    }
}
