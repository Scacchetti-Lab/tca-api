package com.api.tca.domain.email.service;

import com.api.tca.domain.email.dto.template.TemplateDto;
import com.api.tca.domain.email.entity.TemplateEntity;
import com.api.tca.domain.email.exception.TemplateNotFound;
import com.api.tca.domain.email.repository.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TemplateService {

    @Autowired
    private TemplateRepository templateRepository;

    public TemplateDto getTemplateDto(UUID id) {
        var template = templateRepository.findById(id).orElseThrow(() -> new TemplateNotFound("Template não encontrado"));
        return new TemplateDto(template);
    }

    public TemplateDto getTemplateDto(String subject) {
        var template = templateRepository.findTemplateBySubject(subject).orElseThrow(() -> new TemplateNotFound("Template não encontrado"));
        return new TemplateDto(template);
    }

    public TemplateEntity getTemplateEntity(UUID id) {
        return templateRepository.findById(id).orElseThrow(() -> new TemplateNotFound("Template não encontrado"));
    }

    public TemplateEntity getTemplateEntity(String subject) {
        return templateRepository.findTemplateBySubject(subject).orElseThrow(() -> new TemplateNotFound("Template não encontrado"));
    }
}
