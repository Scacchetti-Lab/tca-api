package com.api.tca.domain.meeting.dto.response.predict;

import com.api.tca.domain.meeting.entity.MeetingPredictEntity;
import com.api.tca.domain.meeting.enums.PredictProcessStatus;

import java.util.UUID;

public record MeetingPredictDto(
        UUID id,
        String predict,
        PredictProcessStatus status,
        Boolean reprocess
) {

    public MeetingPredictDto(MeetingPredictEntity dto) {
        this(
                dto.getId(),
                dto.getPredict(),
                dto.getStatus(),
                dto.getReprocess()
        );
    }
}
