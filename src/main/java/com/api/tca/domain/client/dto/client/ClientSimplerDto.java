package com.api.tca.domain.client.dto.client;

import com.api.tca.domain.client.entity.ClientEntity;
import com.api.tca.domain.client.enums.ClientStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.UUID;

public record ClientSimplerDto(
        UUID id,
        String name,
        String cnpj,
        String squadName,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        BigDecimal revenue, // faturamento
        ClientStatus status,
        String email,
        String phone
) {

    public ClientSimplerDto(ClientEntity client) {
        this(
                client.getId(),
                client.getFantasyName() == null ? client.getName() : client.getFantasyName(),
                client.getCnpj(),
                client.getSquad().getName(),
                client.getRevenue(),
                client.getStatus(),
                client.getEmail(),
                client.getPhone()
        );
    }
}
