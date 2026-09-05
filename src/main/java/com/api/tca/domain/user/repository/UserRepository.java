package com.api.tca.domain.user.repository;

import com.api.tca.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    UserEntity findUserByEmailAndIsDeletedFalse(String email);
    UserEntity findUserByUsernameAndIsDeletedFalse(String username);

    UserEntity findUserByIdAndIsDeletedFalse(UUID id);

}
