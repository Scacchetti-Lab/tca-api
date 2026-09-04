package com.api.tca.common.model;

public class SuccessResult<T> extends ApiResponse<T> {
    public SuccessResult(String message, T content) {
        super("200", message, content, true);
    }
}
