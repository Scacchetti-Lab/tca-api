package com.api.tca.domain.client.dto;

import com.api.tca.domain.client.entity.ClientEntity;
import com.api.tca.domain.client.enums.ClientStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;

public record ClientSimplerDto(
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
