package com.api.tca.domain.meeting.exception.rules;

public class InvalidFileUploadedException extends IllegalArgumentException {
    public InvalidFileUploadedException(String message) {
        super(message);
    }
}
