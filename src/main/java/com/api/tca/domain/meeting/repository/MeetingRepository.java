package com.api.tca.domain.meeting.repository;

import com.api.tca.domain.meeting.entity.MeetingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface MeetingRepository extends JpaRepository<MeetingEntity, UUID> {
    Page<MeetingEntity> findAllByIsDeletedFalse(Pageable pageable);
    Optional<MeetingEntity> findByIdAndIsDeletedFalse(UUID id);

    @Query("SELECT m FROM MeetingEntity m JOIN m.users u WHERE u.id = :userId AND m.isDeleted = false")
    Page<MeetingEntity> findAllByUserIdAndIsDeletedFalse(Pageable pageable, UUID userId);

    @Query("SELECT m FROM MeetingEntity m JOIN m.users u " +
            "WHERE u.id = :userId AND m.scheduled > CURRENT_TIMESTAMP AND m.isDeleted = false " +
            "ORDER BY m.scheduled ASC")
    Optional<MeetingEntity> findFirstNextByUserId(@Param("userId") UUID userId);

    @Query("SELECT m FROM MeetingEntity m JOIN m.users u " +
            "WHERE u.id = :userId AND m.scheduled <= CURRENT_TIMESTAMP " +
            "ORDER BY m.scheduled DESC")
    Optional<MeetingEntity> findFirstLastByUserId(@Param("userId") UUID userId);

    Page<MeetingEntity> findAllByClientIdAndIsDeletedFalse(Pageable pageable, UUID clientId);
    Set<MeetingEntity> findAllByClientIdAndIsDeletedFalse(UUID clientId);

    Boolean existsMeetingsByTotvsIdAndIsDeletedFalse(String totvsId);

    @Query("SELECT COUNT(m) > 0 FROM MeetingEntity m JOIN m.users u WHERE u.id = :userId AND m.id = :meetingId AND u.isDeleted = false")
    boolean isUserInMeeting(@Param("userId") UUID userId, @Param("meetingId") UUID meetingId);
}
