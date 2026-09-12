package com.api.tca.domain.email.dto.email;

import com.api.tca.common.helpers.BrazilRealTime;
import com.api.tca.domain.email.entity.EmailEntity;
import com.api.tca.domain.email.enums.EmailStatus;

import java.time.LocalDateTime;

public record EmailResponseDto(String from, String to, String subject, LocalDateTime sendOn, EmailStatus status, Boolean emailSent) {

    public EmailResponseDto(EmailEntity email) {
        this(
                email.getFrom(),
                email.getTo(),
                email.getSubject(),
                BrazilRealTime.now(),
                email.getStatus(),
                false
        );
    }
}
