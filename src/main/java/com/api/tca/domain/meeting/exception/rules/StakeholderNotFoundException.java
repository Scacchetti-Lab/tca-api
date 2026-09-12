package com.api.tca.domain.meeting.exception.rules;

public class StakeholderNotFoundException extends RuntimeException {
    public StakeholderNotFoundException(String message) {
        super(message);
    }
}
