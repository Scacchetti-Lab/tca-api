package com.api.tca.domain.meeting.dto.request;

import com.api.tca.domain.meeting.enums.MeetingStakeholderSide;
import com.api.tca.domain.meeting.enums.MeetingUserSource;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StakeholderRegisterDto(
        @NotBlank
        String name,

        String email,
        String userName,

        @Enumerated(EnumType.STRING)
        MeetingStakeholderSide stakeholderSide,

        String stakeholderRole,

        @NotNull
        @Enumerated(EnumType.STRING)
        MeetingUserSource source
) {
}
