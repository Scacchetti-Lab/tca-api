package com.api.tca.domain.email.entity;

import com.api.tca.common.helpers.BrazilRealTime;
import com.api.tca.domain.email.dto.template.TemplateDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "template")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class TemplateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String subject;
    private String body;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;


    public TemplateEntity(TemplateDto dto) {
        this.subject = dto.subject();
        this.body = dto.body();
        this.createdOn = BrazilRealTime.now();
    }
}
