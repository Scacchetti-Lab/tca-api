package com.api.tca.domain.meeting.dto.request;

import com.api.tca.domain.transcript.dto.TranscriptFormDataDto;
import com.api.tca.domain.transcript.enums.TranscriptFileType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Set;

public record MinimalRegisterMeetingDto(
    String totvsId,

    @NotBlank
    String title,

    @NotNull
    @PastOrPresent
    LocalDateTime scheduledAt,

    @NotBlank
    String clientName,

    String clientRepresent,

    Set<StakeholderRegisterDto> employees,

    @Valid
    TranscriptFormDataDto transcriptData
) { }
