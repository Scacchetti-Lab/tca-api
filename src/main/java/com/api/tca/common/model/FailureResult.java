package com.api.tca.common.model;

public class FailureResult<T> extends ApiResponse<T> {
    public FailureResult(String message, T content) {
        super("400", message, content, false);
    }
}
