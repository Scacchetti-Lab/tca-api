package com.api.tca.domain.score.repository;

import com.api.tca.domain.meeting.entity.MeetingAnalyseStrategicEntity;
import com.api.tca.domain.score.entity.StrategicScoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface StrategicScoreRepository extends JpaRepository<StrategicScoreEntity, UUID> {
    List<StrategicScoreEntity> findAllByClientId(UUID clientId);

    @Query("SELECT COUNT(ss) FROM StrategicScoreEntity ss WHERE ss.client.id = :clientId")
    Integer countByClientId(@Param("clientId") UUID clientId);
}
