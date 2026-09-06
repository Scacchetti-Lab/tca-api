package com.api.tca.common.model;

import org.springframework.http.HttpStatus;

import java.net.http.HttpClient;

public class FailureResult<T> extends ApiResponse<T> {
    public FailureResult(HttpClient status, String message, T content) {
        super(String.valueOf(status), message, content, false);
    }

    public FailureResult(String message) {
        super(String.valueOf(HttpStatus.BAD_REQUEST), message, null, false);
    }
}
