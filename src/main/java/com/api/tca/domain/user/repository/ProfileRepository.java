package com.api.tca.domain.user.repository;

import com.api.tca.domain.user.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProfileRepository extends JpaRepository<ProfileEntity, UUID> {
    ProfileEntity findProfileByName(String name);
}
