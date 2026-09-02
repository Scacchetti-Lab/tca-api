package com.api.tca.domain.user.repository;

import com.api.tca.domain.user.entity.UserEntity;
import com.api.tca.domain.user.security.UserSecurity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    UserSecurity findByEmail(String username);
}
