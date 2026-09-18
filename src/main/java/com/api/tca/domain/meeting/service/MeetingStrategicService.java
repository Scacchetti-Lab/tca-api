package com.api.tca.domain.meeting.service;


import com.api.tca.common.ai.dto.response.transcript.strategic.StrategicAnalyseDto;
import com.api.tca.domain.meeting.entity.MeetingAnalyseStrategicEntity;
import com.api.tca.domain.meeting.entity.MeetingEntity;
import com.api.tca.domain.meeting.mapper.MeetingStrategicRepository;
import com.api.tca.domain.meeting.repository.MeetingRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MeetingStrategicService {

    @Autowired
    private MeetingStrategicRepository repository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Transactional
    public MeetingAnalyseStrategicEntity addMeetingStrategicAnalysed(MeetingEntity meeting, StrategicAnalyseDto analyseDto) {
        MeetingAnalyseStrategicEntity strategicEntity = new MeetingAnalyseStrategicEntity(meeting, analyseDto);
        return repository.save(strategicEntity);
    }
}
