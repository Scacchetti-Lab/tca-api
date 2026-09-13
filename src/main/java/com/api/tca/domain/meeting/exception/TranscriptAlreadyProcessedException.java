package com.api.tca.domain.meeting.exception;

public class TranscriptAlreadyProcessedException extends RuntimeException {
    public TranscriptAlreadyProcessedException(String message) {
        super(message);
    }
}
