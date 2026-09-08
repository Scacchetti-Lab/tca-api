package com.api.tca.common.ai.dto.request;

import com.api.tca.domain.client.enums.ClientStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.util.Set;

public record ClientContextRequestDto(
        String name,

        @Enumerated(EnumType.STRING)
        ClientStatus status,

        // TODO: Procurar pelo Id do cliente, os sumários de todas as últimas reuniões que ele teve e mapear para esse Set<String>
        @JsonProperty("previous_meetings")
        Set<String> lastMeetingSummaries
) {
}
