package com.api.tca.domain.transcript.repository;

import com.api.tca.domain.transcript.entity.TranscriptEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TranscriptRepository extends JpaRepository<TranscriptEntity, UUID> {

}
