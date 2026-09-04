package com.api.tca.domain.user.service;

import com.api.tca.domain.user.entity.ProfileEntity;
import com.api.tca.domain.user.enums.ProfileTypes;
import com.api.tca.domain.user.exception.ProfileNotFound;
import com.api.tca.domain.user.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    public ProfileEntity getProfileByKey(ProfileTypes profileType) {
        var formatName = profileType.name().toUpperCase().charAt(0) + profileType.name().substring(1).toLowerCase();
        var profile = profileRepository.findProfileByName(formatName + " Profile");

        if (profile == null)
            throw new ProfileNotFound("Perfil não encontrado");
        return profile;
    }
}
