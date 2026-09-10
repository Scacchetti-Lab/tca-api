package com.api.tca.domain.address.dto;

public record ClientAddressDto(
        String name,
        String number,
        String neighborhood,
        String uf
) {}
