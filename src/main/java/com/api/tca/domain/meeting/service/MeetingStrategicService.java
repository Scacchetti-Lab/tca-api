package com.api.tca.domain.meeting.service;


import com.api.tca.common.ai.dto.response.transcript.strategic.StrategicAnalyseDto;
import com.api.tca.domain.meeting.entity.MeetingAnalyseStrategicEntity;
import com.api.tca.domain.meeting.entity.MeetingEntity;
import com.api.tca.domain.meeting.repository.MeetingStrategicRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MeetingStrategicService {

    @Autowired
    private MeetingStrategicRepository repository;

    @Transactional
    public MeetingAnalyseStrategicEntity addMeetingStrategicAnalysed(MeetingEntity meeting, StrategicAnalyseDto analyseDto) {
        MeetingAnalyseStrategicEntity strategicEntity = new MeetingAnalyseStrategicEntity(meeting, analyseDto);
        return repository.save(strategicEntity);
    }

    public List<MeetingAnalyseStrategicEntity> getAllMeetingByClientId(UUID clientId) {
        return repository.findAllByClientIdOrderByMeeting_ScheduledAsc(clientId);
    }
}
