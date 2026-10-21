package com.api.tca.domain.user.dto.user;

import com.api.tca.domain.address.dto.AddressDto;
import jakarta.validation.constraints.Pattern;

public record UpdateUserDto(
        String username,
        @Pattern(regexp = "^\\+?[\\d\\s\\-()]+$")
        String mobilePhone,
        String profilePhoto,
        Boolean useMfa,
        @Pattern(regexp = "TCA-SQD-[0-9]+")
        String squadCode,

        AddressDto address
) { }
