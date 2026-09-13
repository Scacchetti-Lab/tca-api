package com.api.tca.common.exception.custom;

public class FailOnPredictException extends RuntimeException {
    public FailOnPredictException(String message) {
        super(message);
    }
}
