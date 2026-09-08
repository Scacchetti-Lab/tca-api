package com.api.tca.domain.meeting.repository;

import com.api.tca.domain.meeting.entity.MeetingEntity;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface MeetingRepository extends JpaRepository<MeetingEntity, UUID> {
    @Query("SELECT m FROM MeetingEntity m JOIN m.users u WHERE u.id = :userId")
    Page<MeetingEntity> findAllByUserId(Pageable pageable, UUID userId);

    @Query("SELECT m FROM MeetingEntity m JOIN m.users u " +
            "WHERE u.id = :userId AND m.scheduled > CURRENT_TIMESTAMP " +
            "ORDER BY m.scheduled ASC")
    Optional<MeetingEntity> findFirstNextByUserId(@Param("userId") UUID userId);

    @Query("SELECT m FROM MeetingEntity m JOIN m.users u " +
            "WHERE u.id = :userId AND m.scheduled <= CURRENT_TIMESTAMP " +
            "ORDER BY m.scheduled DESC")
    Optional<MeetingEntity> findFirstLastByUserId(@Param("userId") UUID userId);

    Page<MeetingEntity> findAllByClientId(Pageable pageable, UUID clientId);

    Boolean existsMeetingsByTotvsId(String totvsId);

    @Query("SELECT COUNT(m) > 0 FROM MeetingEntity m JOIN m.users u WHERE u.id = :userId AND u.isDeleted = false")
    Boolean existsStakeholderByUserId(@Param("userId") UUID userId);
}
