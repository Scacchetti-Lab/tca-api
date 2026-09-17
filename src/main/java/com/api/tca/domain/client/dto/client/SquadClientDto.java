package com.api.tca.domain.client.dto.client;

import com.api.tca.domain.client.entity.ClientEntity;

import java.util.UUID;

public record SquadClientDto(
        UUID id,
        String fantasyName,
        String segment,
        String email
) {
    public SquadClientDto(ClientEntity client) {
        this(
            client.getId(),
            client.getFantasyName(),
            client.getSegment(),
            client.getEmail()
        );
    }
}
