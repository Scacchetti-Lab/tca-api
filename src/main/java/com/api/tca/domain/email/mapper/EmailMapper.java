package com.api.tca.domain.email.mapper;

import com.api.tca.domain.email.dto.email.EmailRequestDto;
import com.api.tca.domain.email.dto.maileroo.MailerooDto;
import com.api.tca.domain.email.entity.EmailEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, componentModel = "spring")
public interface EmailMapper {

    @Mapping(target = "id", ignore = true)
    EmailEntity mapEmailRequestDtoToEmailEntity(EmailRequestDto requestDto);
}
