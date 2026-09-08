package com.api.tca.domain.meeting.exception;

public class MeetingAlreadyExistsException extends RuntimeException {
    public MeetingAlreadyExistsException(String message) {
        super(message);
    }
}
