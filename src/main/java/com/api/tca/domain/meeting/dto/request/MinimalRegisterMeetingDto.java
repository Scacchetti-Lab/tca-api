package com.api.tca.domain.meeting.dto.request;

import com.api.tca.domain.transcript.enums.TranscriptFileType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record MinimalRegisterMeetingDto(
    String totvsId,

    @NotNull
    LocalDateTime scheduledAt,

    @NotBlank
    String clientName,

    @NotBlank
    String clientRepresent,

    @NotBlank
    Set<EmployeeMeetingDto> employees,

//    @NotNull(message = "Arquivo de trasncrição obrigatório")
//    MultipartFile transcript,

    @NotNull(message = "Tipo do arquivo de trasncrição obrigatório")
    @Enumerated(EnumType.STRING)
    TranscriptFileType fileType
) { }
