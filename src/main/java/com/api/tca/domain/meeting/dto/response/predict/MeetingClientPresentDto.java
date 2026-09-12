package com.api.tca.domain.meeting.dto.response.predict;

import com.api.tca.domain.client.entity.ClientEntity;
import com.api.tca.domain.client.enums.ClientStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

public record MeetingClientPresentDto(
        String fantasyName,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String clientRepresent,
        String cnpj,
        String email,
        String phone,
        ClientStatus clientStatus
) {
    public MeetingClientPresentDto(ClientEntity client, String clientRepresent) {
        this (
                client.getFantasyName(),
                clientRepresent,
                client.getCnpj(),
                client.getEmail(),
                client.getPhone(),
                client.getStatus()
        );
    }

}

