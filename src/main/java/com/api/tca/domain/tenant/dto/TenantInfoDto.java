package com.api.tca.domain.tenant.dto;

import com.api.tca.domain.tenant.enums.TenantStatus;

import java.util.UUID;

public record TenantInfoDto(
        UUID id,
        String fantasyName,
        String segment,
        String email,
        TenantStatus status
) {
}
