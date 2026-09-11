package com.api.tca.domain.chat.exception;

public class MessageSendFailedException extends RuntimeException {
    public MessageSendFailedException(String message) {
        super(message);
    }
}
