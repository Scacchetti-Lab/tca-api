package com.api.tca.common.ai.dto.request.predict;

import java.util.UUID;

public record PredictEventRequestDto(
        UUID entityId,
        Boolean reprocess
) {
}
