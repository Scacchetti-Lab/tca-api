package com.api.tca.domain.client.repository;

import com.api.tca.domain.client.entity.ClientAnalyseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ClientAnalyseRepository extends JpaRepository<ClientAnalyseEntity, UUID> {
    @Query("SELECT ca FROM ClientAnalyseEntity ca JOIN ClientEntity c ON c.id = ca.client.id WHERE c.isDeleted = false AND c.id = :id")
    Optional<ClientAnalyseEntity> findByClientId(@Param("id") UUID clientId);
}
