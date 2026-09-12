package com.api.tca.domain.user.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthenticateDto(@NotBlank String email, @NotBlank String password) { }
