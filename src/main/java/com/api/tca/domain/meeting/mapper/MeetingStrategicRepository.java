package com.api.tca.domain.meeting.mapper;

import com.api.tca.domain.meeting.entity.MeetingAnalyseStrategicEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MeetingStrategicRepository extends JpaRepository<MeetingAnalyseStrategicEntity, UUID> { }
