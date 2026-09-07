package com.api.tca.common.ai.dto.response;

public record ResponseModelDto<T>(Integer status, T content, String message, Boolean isValid) {
}
