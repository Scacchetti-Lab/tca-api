package com.api.tca.domain.user.exception;

public class PasswordsAreEquals extends RuntimeException {
    public PasswordsAreEquals(String message) {
        super(message);
    }
}
