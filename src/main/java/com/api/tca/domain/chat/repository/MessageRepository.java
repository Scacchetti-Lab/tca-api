package com.api.tca.domain.chat.repository;

import com.api.tca.domain.chat.entity.AiMessagesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<AiMessagesEntity, UUID> {
    List<AiMessagesEntity> findTop3BySessionIdOrderByCreatedOnDesc(UUID sessionId);
    Page<AiMessagesEntity> findAllBySessionIdOrderByCreatedOnAsc(UUID sessionId, Pageable pageable);
}
