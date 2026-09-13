package com.api.tca.domain.meeting.repository;

import com.api.tca.domain.meeting.entity.MeetingPredictEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface MeetingPredictRepository extends JpaRepository<MeetingPredictEntity, UUID> {
    Optional<MeetingPredictEntity> findByMeetingId(UUID meetingId);
}
