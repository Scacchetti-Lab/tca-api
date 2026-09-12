package com.api.tca.domain.email.exception;

public class TemplateNotFound extends RuntimeException {
    public TemplateNotFound(String message) {
        super(message);
    }
}
