package com.api.tca.domain.client.dto.predict;

import com.api.tca.domain.client.entity.ClientPredictEntity;
import com.api.tca.domain.meeting.enums.PredictProcessStatus;

import java.util.UUID;

public record ClientPredictResponseDto(
        UUID id,
        String predict,
        PredictProcessStatus status,
        Boolean reprocess
) {
    public ClientPredictResponseDto(ClientPredictEntity dto) {
        this(
                dto.getId(),
                dto.getPredict(),
                dto.getStatus(),
                dto.getReprocess()
        );
    }
}
