package com.api.tca.domain.meeting.exception;

public class StakeholderNotFoundException extends RuntimeException {
    public StakeholderNotFoundException(String message) {
        super(message);
    }
}
