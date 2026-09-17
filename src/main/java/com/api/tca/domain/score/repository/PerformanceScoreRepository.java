package com.api.tca.domain.score.repository;

import com.api.tca.domain.score.entity.PerformanceScoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PerformanceScoreRepository extends JpaRepository<PerformanceScoreEntity, UUID> {
}
