package com.api.tca.domain.chat.exception;

public class SessionCreateFailedException extends RuntimeException {
    public SessionCreateFailedException(String message) {
        super(message);
    }
}
