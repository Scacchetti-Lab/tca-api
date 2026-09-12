package com.api.tca.domain.user.exception;

public class ProfileNotFound extends RuntimeException {
    public ProfileNotFound(String message) {
        super(message);
    }
}
