package com.api.tca.domain.salesperson.repository;

import com.api.tca.domain.salesperson.entity.SalespersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SalespersonRepository extends JpaRepository<SalespersonEntity, UUID> {
    Optional<SalespersonEntity> findByUserId(UUID userId);
}
