package com.api.tca.common.ai.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record ClientEmbeddingRequest(@JsonProperty("client_id") UUID clientId) { }
