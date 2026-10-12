package com.api.tca.domain.meeting.repository;

import com.api.tca.domain.meeting.entity.MeetingClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MeetingClientRepository extends JpaRepository<MeetingClientEntity, UUID> { }
