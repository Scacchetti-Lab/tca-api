package com.api.tca.domain.user.dto.user;

import com.api.tca.domain.user.dto.profile.ProfileDto;
import com.api.tca.domain.user.entity.UserEntity;

import java.util.List;
import java.util.UUID;

public record RegisterResponseDto(UUID id, String fullName, String userName, String email, List<ProfileDto> profile) {

     public RegisterResponseDto(UserEntity user) {
        this(user.getId(),
                user.getFullName(),
                user.getUsername(),
                user.getEmail(),
                user.getProfiles().stream().map(ProfileDto::new).toList());
    }
}
