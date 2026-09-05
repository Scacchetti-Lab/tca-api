package com.api.tca.domain.user.repository;

import com.api.tca.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    UserEntity findUserByEmailAndIsDeletedFalse(String email);
    UserEntity findUserByUsernameAndIsDeletedFalse(String username);

    UserEntity findUserByIdAndIsDeletedFalse(UUID id);

}
