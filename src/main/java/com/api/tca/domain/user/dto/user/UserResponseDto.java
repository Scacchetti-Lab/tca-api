package com.api.tca.domain.user.dto.user;

import com.api.tca.domain.address.dto.AddressDto;
import com.api.tca.domain.user.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String fullName,
        LocalDate birthDate,
        String userName,
        String email,
        String cpf,
        String mobilePhone,
        String profilePhotoUrl,
        int score,
        Boolean useMfa,
        AddressDto address
) {

    public UserResponseDto(UserEntity user) {
        this(
            user.getId(),
            user.getFullName(),
            user.getBirthDate(),
            user.getUsername(),
            user.getEmail(),
            user.getCpf(),
            user.getMobilePhone(),
            user.getProfilePhotoUrl(),
            user.getScore(),
            user.getUseMfa(),
            new AddressDto(user.getAddress())
        );
    }
}
