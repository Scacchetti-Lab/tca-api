package com.api.tca.common.ai.dto.request.predict;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record ClientPredictDto(@JsonProperty("client_id") UUID clientId) {
}
