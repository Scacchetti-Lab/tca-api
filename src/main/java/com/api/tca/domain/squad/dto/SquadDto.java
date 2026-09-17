package com.api.tca.domain.squad.dto;

import com.api.tca.domain.client.dto.client.SquadClientDto;
import com.api.tca.domain.squad.entity.SquadEntity;

import java.util.List;

public record SquadDto(
        String code,
        String name,
        String description,
        double performance,
        List<SquadClientDto> clients
) {
    public SquadDto(SquadEntity entity) {
        this(
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getPerformance(),
                entity.getClients().stream().map(SquadClientDto::new).toList()
        );
    }
}
