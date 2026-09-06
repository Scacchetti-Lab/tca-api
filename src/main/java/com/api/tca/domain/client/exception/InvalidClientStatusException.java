package com.api.tca.domain.client.exception;

public class InvalidClientStatusException extends RuntimeException {
    public InvalidClientStatusException(String message) {
        super(message);
    }
}
