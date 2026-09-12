package com.api.tca.domain.client.dto.analyse;

import com.api.tca.domain.address.dto.ClientAddressDto;
import com.api.tca.domain.client.dto.client.ClientDescriptionDto;
import com.api.tca.domain.client.enums.ClientStatus;
import com.api.tca.domain.client.enums.FinancialImpact;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.UUID;

public record ClientDetailedDto(
        @Valid ClientDescriptionDto details,
        @Valid ClientAnalysisDto analyse
) {
    public ClientDetailedDto(UUID id, String name, String fantasyName, String cnpj, String email, String phone,
                             BigDecimal revenue, String segment, ClientStatus status, BigDecimal performance,
                             BigDecimal closingProbability, BigDecimal flexibility, BigDecimal risk,
                             BigDecimal financialImpact, FinancialImpact financialStatus,
                             String squadName)
    {
        this(
               new ClientDescriptionDto(
                       id,
                       name,
                       fantasyName,
                       cnpj,
                       email,
                       phone,
                       revenue,
                       squadName,
                       segment,
                       status
               ),
                new ClientAnalysisDto(
                     closingProbability,
                     financialImpact,
                     flexibility,
                     financialStatus,
                     performance,
                     risk
                )
        );
    }


}
