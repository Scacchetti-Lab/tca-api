package com.api.tca.domain.client.dto;

import com.api.tca.domain.client.enums.ClientStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.UUID;

public record ClientCompleteDto(@Valid ClientDetailedDto details, ClientAnalyseDto analyse) {
}
