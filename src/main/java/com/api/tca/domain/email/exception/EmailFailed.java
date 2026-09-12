package com.api.tca.domain.email.exception;

public class EmailFailed extends RuntimeException {
    public EmailFailed(String message) {
        super(message);
    }
}
