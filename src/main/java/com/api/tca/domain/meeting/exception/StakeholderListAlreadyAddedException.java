package com.api.tca.domain.meeting.exception;

public class StakeholderListAlreadyAddedException extends RuntimeException {
    public StakeholderListAlreadyAddedException(String message) {
        super(message);
    }
}
