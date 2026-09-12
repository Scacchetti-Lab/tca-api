package com.api.tca.domain.meeting.dto.response;

import com.api.tca.domain.transcript.dto.TranscriptBasicDto;
import jakarta.validation.Valid;

public record MeetingTranscriptBasicDto(
        @Valid
        MeetingBasicDataDto meeting,

        @Valid
        TranscriptBasicDto transcript
) {
}
