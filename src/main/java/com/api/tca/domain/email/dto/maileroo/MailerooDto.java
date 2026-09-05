package com.api.tca.domain.email.dto.maileroo;

public record MailerooDto(Boolean success, String message, Data data) {

    public record Data(String referenceId) {}
}
