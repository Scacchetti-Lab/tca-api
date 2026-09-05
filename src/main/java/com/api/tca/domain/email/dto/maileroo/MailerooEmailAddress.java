package com.api.tca.domain.email.dto.maileroo;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MailerooEmailAddress(
        String address,
        @JsonProperty("display_name") String displayName
) {}
