package com.api.tca.domain.tenant.service;

import com.api.tca.domain.tenant.dto.TenantInfoDto;
import com.api.tca.domain.tenant.entity.TenantEntity;
import com.api.tca.domain.tenant.exception.TenantNotFoundException;
import com.api.tca.domain.tenant.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    public TenantEntity getTenantById(UUID id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new TenantNotFoundException("Tenant não encontrado"));
    }
}
