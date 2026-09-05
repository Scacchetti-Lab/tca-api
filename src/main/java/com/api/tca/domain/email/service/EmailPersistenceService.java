package com.api.tca.domain.email.service;

import com.api.tca.domain.email.configuration.MailerooConfig;
import com.api.tca.domain.email.dto.email.EmailRequestDto;
import com.api.tca.domain.email.entity.EmailEntity;
import com.api.tca.domain.email.enums.EmailStatus;
import com.api.tca.domain.email.mapper.EmailMapper;
import com.api.tca.domain.email.repository.EmailRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmailPersistenceService {

    @Autowired
    private TemplateService templateService;

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private MailerooConfig mailerooConfig;

    @Autowired
    private EmailMapper mapper;

    @Transactional
    protected EmailEntity prepareEmail(String subject,
                                       Map<String, String> bodyReplace,
                                       EmailRequestDto emailDto) {
        var template = templateService.getTemplateEntity(subject);
        var generatedEmail = mapper.mapEmailRequestDtoToEmailEntity(emailDto);

        var replacedBody = template.getBody();
        for (var entry : bodyReplace.entrySet()) {
            replacedBody = replacedBody.replace(entry.getKey(), entry.getValue());
        }

        generatedEmail.setSubject(template.getSubject());
        generatedEmail.setFrom(mailerooConfig.getSmtpHost());
        generatedEmail.setBody(replacedBody);
        generatedEmail.setStatus(EmailStatus.PENDING);
        generatedEmail.setTemplate(template);

        return emailRepository.save(generatedEmail);
    }

    @Transactional
    protected void updateStatus(UUID id, EmailStatus status) {
        var email = emailRepository.findById(id).orElseThrow();
        email.setStatus(status);
    }
}
