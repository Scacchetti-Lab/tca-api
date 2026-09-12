package com.api.tca.domain.client.dto.client;

import com.api.tca.domain.address.dto.AddressDto;
import com.api.tca.domain.squad.dto.RegisterSquadDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record RegisterClientRequestDto(
        @NotBlank
        @Pattern(regexp = "([A-Z0-9]{2}[.]?[A-Z0-9]{3}[.]?[A-Z0-9]{3}/?[A-Z0-9]{4}-?[0-9]{2})|([A-Z0-9]{3}[.]?[A-Z0-9]{3}[.]?[A-Z0-9]{3}-?[0-9]{2})")
        String cnpj,

        @NotBlank
        String name,

        @NotBlank
        String fantasyName,

        @Valid
        @NotNull
        AddressDto address,

        @Valid
        @NotNull
        RegisterSquadDto squad,

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        BigDecimal revenue,

        @Email
        String email,

        @Pattern(regexp = "^\\+?[\\d\\s\\-()]+$")
        String phone
) {
}
