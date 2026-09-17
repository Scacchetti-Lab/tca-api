package com.api.tca.domain.score.repository;

import com.api.tca.domain.score.entity.StrategicScoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StrategicScoreRepository extends JpaRepository<StrategicScoreEntity, UUID> {
}
