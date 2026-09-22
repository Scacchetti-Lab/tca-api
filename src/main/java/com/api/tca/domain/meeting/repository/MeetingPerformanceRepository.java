package com.api.tca.domain.meeting.repository;

import com.api.tca.domain.meeting.entity.MeetingAnalysePerformanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MeetingPerformanceRepository extends JpaRepository<MeetingAnalysePerformanceEntity, UUID> { }
