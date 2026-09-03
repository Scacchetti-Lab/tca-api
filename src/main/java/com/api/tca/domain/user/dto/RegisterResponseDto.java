package com.api.tca.domain.user.dto;

import com.api.tca.domain.user.entity.UserEntity;
import com.api.tca.domain.user.enums.ScoreType;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record RegisterResponseDto(UUID id, String fullName, String userName, String email, List<ProfileDto> profile, ScoreType scoreType) {

     public RegisterResponseDto(UserEntity user) {
        this(user.getId(),
                user.getFullName(),
                user.getUsername(),
                user.getEmail(),
                user.getProfiles().stream().map(ProfileDto::new).toList(),
                user.getScoreType());
    }
}
