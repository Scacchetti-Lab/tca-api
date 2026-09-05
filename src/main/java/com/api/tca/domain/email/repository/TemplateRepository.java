package com.api.tca.domain.email.repository;

import com.api.tca.domain.email.entity.TemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TemplateRepository extends JpaRepository<TemplateEntity, UUID> {
    Optional<TemplateEntity> findTemplateBySubject(String name);
}
