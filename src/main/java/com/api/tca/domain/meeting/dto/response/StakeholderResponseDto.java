package com.api.tca.domain.meeting.dto.response;

import com.api.tca.domain.meeting.dto.request.StakeholderRegisterDto;
import com.api.tca.domain.meeting.enums.MeetingUserSource;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public record StakeholderResponseDto(
        String name,
        @Enumerated(EnumType.STRING)
        MeetingUserSource source
) {
        public StakeholderResponseDto(StakeholderRegisterDto dto) {
                this(dto.name(), dto.source());
        }
}
