package com.api.tca.domain.tenant.repository;

import com.api.tca.domain.tenant.entity.TenantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TenantRepository extends JpaRepository<TenantEntity, UUID> {
}
