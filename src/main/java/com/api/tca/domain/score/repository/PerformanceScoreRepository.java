package com.api.tca.domain.score.repository;

import com.api.tca.domain.score.entity.PerformanceScoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PerformanceScoreRepository extends JpaRepository<PerformanceScoreEntity, UUID> {

    List<PerformanceScoreEntity> findTop4BySalespersonIdOrderByMeeting_ScheduledDesc(UUID salespersonId);

    List<PerformanceScoreEntity> findAllBySalespersonIdOrderByMeeting_ScheduledAsc(UUID salespersonId);

    @Query("SELECT DISTINCT p.salesperson.id FROM PerformanceScoreEntity p")
    List<UUID> findDistinctSalespersonIds();
}
