package com.api.tca.common.model;

import org.springframework.http.HttpStatus;

public class SuccessResult<T> extends ApiResponse<T> {
    public SuccessResult(HttpStatus status, String message, T content) {
        super(String.valueOf(status), message, content, true);
    }

    public SuccessResult(String message, T content) {
        this(HttpStatus.OK, message, content);
    }
}
