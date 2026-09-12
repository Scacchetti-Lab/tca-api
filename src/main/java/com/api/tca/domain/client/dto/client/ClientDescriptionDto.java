package com.api.tca.domain.client.dto.client;

import com.api.tca.domain.client.entity.ClientEntity;
import com.api.tca.domain.client.enums.ClientStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.UUID;

public record ClientDescriptionDto(
        UUID id,
        String name,
        String fantasyName,
        String cnpj,
        String email,
        String phone,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        BigDecimal revenue,
        String squadName,
        String segment,
        ClientStatus status
) {

    public ClientDescriptionDto(ClientEntity client) {
        this(
            client.getId(),
            client.getName(),
            client.getFantasyName(),
            client.getCnpj(),
            client.getEmail(),
            client.getPhone(),
            client.getRevenue(),
            client.getSquad().getName(),
            client.getSegment(),
            client.getStatus()
        );
    }
}
