package com.api.tca.domain.meeting.repository;

import com.api.tca.domain.meeting.entity.MeetingAnalyseStrategicEntity;
import com.api.tca.domain.meeting.entity.MeetingEntity;
import com.api.tca.domain.meeting.entity.MeetingPredictEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface MeetingRepository extends JpaRepository<MeetingEntity, UUID> {
    Page<MeetingEntity> findAllByIsDeletedFalse(Pageable pageable);
    Optional<MeetingEntity> findByIdAndIsDeletedFalse(UUID id);

    @Query("SELECT m FROM MeetingEntity m JOIN FETCH m.client WHERE m.id = :id AND m.isDeleted = false")
    Optional<MeetingEntity> findByIdWithClient(UUID id);

    @Query("SELECT m FROM MeetingEntity m JOIN m.users u WHERE u.id = :userId AND m.isDeleted = false")
    Page<MeetingEntity> findAllByUserIdAndIsDeletedFalse(Pageable pageable, UUID userId);

    @Query("SELECT m FROM MeetingEntity m JOIN m.users u " +
            "WHERE u.id = :userId " +
            "AND m.scheduled > CURRENT_TIMESTAMP " +
            "AND m.isDeleted = false " +
            "ORDER BY m.scheduled ASC")
    List<MeetingEntity> findFirstNextByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT m FROM MeetingEntity m JOIN m.users u " +
            "WHERE u.id = :userId " +
            "AND m.scheduled <= CURRENT_TIMESTAMP " +
            "ORDER BY m.scheduled DESC")
    List<MeetingEntity> findFirstLastByUserId(@Param("userId") UUID userId, Pageable pageable);

    Page<MeetingEntity> findAllByClientIdAndIsDeletedFalse(Pageable pageable, UUID clientId);
    Set<MeetingEntity> findAllByClientIdAndIsDeletedFalse(UUID clientId);

    Boolean existsMeetingsByTotvsIdAndIsDeletedFalse(String totvsId);

    @Query("SELECT COUNT(m) > 0 FROM MeetingEntity m JOIN m.users u WHERE u.id = :userId AND m.id = :meetingId AND u.isDeleted = false")
    boolean isUserInMeeting(@Param("userId") UUID userId, @Param("meetingId") UUID meetingId);

    @Query("""
        SELECT m FROM MeetingEntity m
        WHERE m.client.id = :clientId
        AND m.status = MeetingStatus.SCHEDULED
        AND m.isDeleted = false
        AND m.scheduled > CURRENT_TIMESTAMP
        ORDER BY m.scheduled ASC
        """)
    List<MeetingEntity> findNextClientMeeting(@Param("clientId") UUID clientId, Pageable pageable);

    @Query("""
        SELECT m FROM MeetingEntity m
        WHERE m.client.id = :clientId
        AND m.status = MeetingStatus.COMPLETED
        AND m.isDeleted = false
        AND m.scheduled <= CURRENT_TIMESTAMP
        ORDER BY m.scheduled DESC
        """)
    List<MeetingEntity> findLastClientMeeting(@Param("clientId") UUID clientId, Pageable pageable);
}
