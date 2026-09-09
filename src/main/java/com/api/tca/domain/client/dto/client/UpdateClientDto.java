package com.api.tca.domain.client.dto.client;

import com.api.tca.domain.address.dto.AddressDto;
import com.api.tca.domain.client.enums.ClientStatus;
import com.api.tca.domain.squad.dto.RegisterSquadDto;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;

public record UpdateClientDto(
        String fantasyName,
        String email,
        String phone,
        String segment,

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        BigDecimal revenue,
        AddressDto address,
        RegisterSquadDto squad,
        ClientStatus status
) {
        public UpdateClientDto(String segment) {
                this(
                        null,
                        null,
                        null,
                        segment,
                        null,
                        null,
                        null,
                        null
                );
        }
}
