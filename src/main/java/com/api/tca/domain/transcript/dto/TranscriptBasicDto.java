package com.api.tca.domain.transcript.dto;

import com.api.tca.domain.transcript.entity.TranscriptEntity;
import com.api.tca.domain.transcript.enums.TranscriptStatus;

import java.util.UUID;

public record TranscriptBasicDto(
        UUID id,
        String resume,
        TranscriptStatus status
) {
    public TranscriptBasicDto(TranscriptEntity entity){
        this(
                entity.getId(),
                entity.getResume(),
                entity.getStatus()
        );
    }
}
