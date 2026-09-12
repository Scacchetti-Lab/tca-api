package com.api.tca.domain.email.service;

import com.api.tca.domain.email.configuration.MailerooConfig;
import com.api.tca.domain.email.dto.email.EmailRequestDto;
import com.api.tca.domain.email.entity.EmailEntity;
import com.api.tca.domain.email.enums.EmailStatus;
import com.api.tca.domain.email.exception.EmailFailed;
import com.api.tca.domain.email.mapper.EmailMapper;
import com.api.tca.domain.email.repository.EmailRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmailService {
    @Autowired
    private MailerooService mailerooService;

    @Autowired
    private EmailPersistenceService emailPersistenceService;

    @Value("${tca.email.send}")
    private Boolean isToSendEmail;

    @Async
    public void sendEmail(String subject,
                          Map<String, String> bodyReplace,
                          EmailRequestDto emailDto,
                          String displayedName) {

        var generatedEmail = emailPersistenceService.prepareEmail(subject, bodyReplace, emailDto);

        if (!isToSendEmail) {
            emailPersistenceService.updateStatus(generatedEmail.getId(), EmailStatus.SKIPPED);
            return;
        }

        try {
            mailerooService.sendEmail(generatedEmail, displayedName);
            emailPersistenceService.updateStatus(generatedEmail.getId(), EmailStatus.SENT);
        } catch (Exception e) {
            emailPersistenceService.updateStatus(generatedEmail.getId(), EmailStatus.FAILED);
        }
    }
}
