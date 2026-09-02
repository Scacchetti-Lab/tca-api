package com.api.tca.domain.address.dto;

import com.api.tca.domain.address.entity.AddressEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record AddressViaCepDto(
        @Pattern(regexp = "(\\d{5})-?(\\d{3})")
        @NotBlank
        String cep,

        @Length(min = 2, max = 5)
        @NotBlank
        String numero,

        @NotBlank
        String logradouro,

        String complemento,

        @NotBlank
        String estado,

        @NotBlank
        @Length(min = 2, max = 2)
        String uf,

        @NotBlank
        String bairro
) {
        public AddressViaCepDto(AddressEntity entity) {
                this(entity.getPostalCode(), entity.getNumber(), entity.getName(), entity.getComplement(), entity.getState(), entity.getUf(), entity.getNeighborhood());
        }
}
