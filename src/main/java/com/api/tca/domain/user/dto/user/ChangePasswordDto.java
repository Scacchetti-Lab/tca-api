package com.api.tca.domain.user.dto.user;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordDto(@NotBlank String oldPassword, @NotBlank String newPassword) {
}
