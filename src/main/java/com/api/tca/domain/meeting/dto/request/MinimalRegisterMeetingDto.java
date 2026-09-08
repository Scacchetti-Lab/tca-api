package com.api.tca.domain.meeting.dto.request;

import com.api.tca.domain.transcript.enums.TranscriptFileType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Set;

public record MinimalRegisterMeetingDto(
    String totvsId,

    @NotNull
    LocalDateTime scheduledAt,

    @NotBlank
    String clientName,

    @NotBlank
    String clientRepresent,

    @Valid
    @NotBlank
    Set<StakeholderRegisterDto> employees,

//    @NotNull(message = "Arquivo de trasncrição obrigatório")
//    MultipartFile transcript,

    @NotNull(message = "Tipo do arquivo de transcrição obrigatório")
    @Enumerated(EnumType.STRING)
    TranscriptFileType fileType
) { }
