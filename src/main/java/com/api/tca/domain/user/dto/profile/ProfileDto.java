package com.api.tca.domain.user.dto.profile;

import com.api.tca.domain.user.entity.ProfileEntity;

public record ProfileDto(String name, boolean isAdmin) {

    public ProfileDto(ProfileEntity profile) {
        this(profile.getName(), profile.isAdmin());
    }
}
