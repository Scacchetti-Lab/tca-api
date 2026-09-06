package com.api.tca.domain.client.dto;

import com.api.tca.domain.client.entity.ClientEntity;
import com.api.tca.domain.client.enums.ClientStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.UUID;

public record ClientDetailedDto(
        UUID id,
        String name,
        String fantasyName,
        String cnpj,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String email,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String phone,
        String squadName,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        BigDecimal revenue,
        ClientStatus status
) {

    public ClientDetailedDto(ClientEntity client) {
        this(
            client.getId(),
            client.getName(),
            client.getFantasyName(),
            client.getCnpj(),
            client.getEmail(),
            client.getPhone(),
            client.getSquad().getName(),
            client.getRevenue(),
            client.getStatus()
        );
    }
}
