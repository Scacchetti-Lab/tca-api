package com.api.tca.domain.meeting.service;

import com.api.tca.common.ai.dto.response.transcript.performance.PerformanceAnalyseDto;
import com.api.tca.domain.meeting.entity.MeetingAnalysePerformanceEntity;
import com.api.tca.domain.meeting.entity.MeetingEntity;
import com.api.tca.domain.meeting.mapper.MeetingPerformanceRepository;
import com.api.tca.domain.meeting.repository.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MeetingPerformanceService {

    @Autowired
    private MeetingPerformanceRepository repository;

    @Autowired
    private MeetingRepository meetingRepository;

    public MeetingAnalysePerformanceEntity addMeetingPerformanceAnalysed(MeetingEntity meeting, PerformanceAnalyseDto perfDto) {
        MeetingAnalysePerformanceEntity performanceEntity = new MeetingAnalysePerformanceEntity(meeting, perfDto);
        return repository.save(performanceEntity);
    }
}
