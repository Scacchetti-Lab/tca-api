package com.api.tca.domain.transcript.exceptions;

public class TranscriptNotFound extends RuntimeException {
    public TranscriptNotFound(String message) {
        super(message);
    }
}
