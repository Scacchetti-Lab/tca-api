package com.api.tca.common.ai.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ResponseModelDto<T>(Integer status, T content, String message, @JsonProperty("is_valid") Boolean isValid) {
}
