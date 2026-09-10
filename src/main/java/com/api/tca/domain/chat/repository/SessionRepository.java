package com.api.tca.domain.chat.repository;

import com.api.tca.domain.chat.entity.AiSessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<AiSessionEntity, UUID> {
    Page<AiSessionEntity> findAllByUserIdAndIsDeletedFalse(UUID userId, Pageable pageable);
    Optional<AiSessionEntity> findByTitleAndUserIdAndIsDeletedFalse(String title, UUID userId);
}
