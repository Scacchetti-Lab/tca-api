package com.api.tca.domain.meeting.exception.rules;

public class MeetingAlreadyExistsException extends RuntimeException {
    public MeetingAlreadyExistsException(String message) {
        super(message);
    }
}
