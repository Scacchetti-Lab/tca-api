package com.api.tca.domain.meeting.repository;

import com.api.tca.domain.meeting.entity.MeetingStakeholdersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface MeetingStakeHolderRepository extends JpaRepository<MeetingStakeholdersEntity, UUID> {
    Set<MeetingStakeholdersEntity> findAllByMeetingId(UUID meetingId);

    Optional<MeetingStakeholdersEntity> findFirstByNameAndMeetingId(String name, UUID meetingId);

    Boolean existsByNameAndMeetingId(String name, UUID meetingId);
}
