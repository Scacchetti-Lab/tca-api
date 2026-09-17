package com.api.tca.domain.user.dto.auth;

import java.util.UUID;

public record TokenRegisterDto(String id, String username, String email, String profile) {
}
