package com.api.tca.domain.client.repository;

import com.api.tca.domain.client.entity.ClientPredictEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientPredictRepository extends JpaRepository<ClientPredictEntity, UUID> {
    Optional<ClientPredictEntity> findByClientId(UUID clientId);
}
