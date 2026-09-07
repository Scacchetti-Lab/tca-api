package com.api.tca.domain.client.dto.analyse;

import com.api.tca.domain.client.dto.client.ClientDetailedDto;
import jakarta.validation.Valid;

public record ClientCompleteDto(@Valid ClientDetailedDto details, ClientAnalyseDto analyse) {
}
