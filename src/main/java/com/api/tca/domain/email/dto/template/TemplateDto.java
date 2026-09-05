package com.api.tca.domain.email.dto.template;

import com.api.tca.domain.email.entity.TemplateEntity;

public record TemplateDto(String subject, String body) {

    public TemplateDto(TemplateEntity template) {
        this(template.getSubject(), template.getBody());
    }
}
