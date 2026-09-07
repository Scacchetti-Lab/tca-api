package com.api.tca.common.ai.dto.response;

public record AiDefaultResponseDto<T>(Integer statusCode, T content, String mediaType) {
}
