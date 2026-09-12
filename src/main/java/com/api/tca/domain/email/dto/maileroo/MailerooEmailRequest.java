package com.api.tca.domain.email.dto.maileroo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MailerooEmailRequest(
        MailerooEmailAddress from,
        Set<MailerooEmailAddress> to,
        String subject,
        String html,
        String plain,
        Boolean tracking,
        String scheduledAt
) {}
