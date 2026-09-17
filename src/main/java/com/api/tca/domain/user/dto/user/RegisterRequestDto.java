package com.api.tca.domain.user.dto.user;

import com.api.tca.domain.address.dto.AddressDto;
import com.api.tca.domain.user.enums.ProfileTypes;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RegisterRequestDto(
        @NotBlank(message = "Nome é obrigatório")
        String fullName,

        @NotBlank(message = "CPF é obrigatório")
        @Pattern(regexp = "(\\d{3}).?(\\d{3}).?(\\d{3})-?(\\d{2})")
        String cpf,

        @Pattern(regexp = "^\\+?[\\d\\s\\-()]+$")
        String mobilePhone,

        @NotNull(message = "Perfil é obrigatório")
        @Enumerated(EnumType.STRING)
        ProfileTypes profileType,

        @NotNull
        @Past(message = "Aniversário não pode ser futuro")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        LocalDate birthDate,

        @NotBlank
        @Email
        String email,

        @NotBlank(message = "Senha é obrigatória")
        String password,

        String profilePhoto,

        @NotNull(message = "Endereço é obrigatório")
        @Valid
        AddressDto address
) {
}
