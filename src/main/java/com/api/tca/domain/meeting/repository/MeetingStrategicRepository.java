package com.api.tca.domain.meeting.repository;

import com.api.tca.domain.meeting.entity.MeetingAnalyseStrategicEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MeetingStrategicRepository extends JpaRepository<MeetingAnalyseStrategicEntity, UUID> {
    List<MeetingAnalyseStrategicEntity> findAllByClientIdOrderByMeeting_ScheduledAsc(UUID clientId);
}
