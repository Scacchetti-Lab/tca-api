package com.api.tca.domain.squad.exceptions;

public class SquadNotFoundException extends RuntimeException {
    public SquadNotFoundException(String message) {
        super(message);
    }
}
