package com.api.tca.domain.user.service;

import com.api.tca.common.security.PasswordGenerator;
import com.api.tca.domain.address.entity.AddressEntity;
import com.api.tca.domain.address.service.AddressService;
import com.api.tca.domain.user.dto.user.*;
import com.api.tca.domain.user.entity.UserEntity;
import com.api.tca.domain.user.enums.UserStatus;
import com.api.tca.domain.user.exception.PasswordsAreEquals;
import com.api.tca.domain.user.exception.UserNotFound;
import com.api.tca.domain.user.mapper.UserMapper;
import com.api.tca.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.abs;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private PasswordGenerator passwordGenerator;

    @Autowired
    private UserMapper mapper;

    public UserEntity getUserById(UUID id) {
        var userEntity = userRepository.findUserByIdAndIsDeletedFalse(id);
        if (userEntity == null)
            throw new UserNotFound("Usuário não encontrado");

        return userEntity;
    }

    @Transactional
    public RegisterResponseDto registerUser(RegisterRequestDto request) {
        AddressEntity userAddress;
        String formattedCep = request.address().postalCode().replace("-", "");
        if (addressService.isAddressExistByPostalCode(formattedCep))
            userAddress = addressService.findAddressByPostalCode(formattedCep);
        else
            userAddress = addressService.createAddress(request.address());

        var userProfile = profileService.getProfileByKey(request.profileType());
        var newUser = userRepository.save(new UserEntity(request, userProfile, userAddress));

        return new RegisterResponseDto(newUser);
    }

    @Transactional
    public UserResponseDto updateUser(UUID id, UpdateUserDto request) {
        var userDb = getUserById(id);
        mapper.mapUpdateDtoToUserEntity(request, userDb);
        userDb.setModifiedOn(LocalDateTime.now());

        return new UserResponseDto(userDb);
    }

    @Transactional
    public void deleteUser(UUID id) {
        var userToDelete = getUserById(id);

        userToDelete.setDeleted(true);
        userToDelete.setModifiedOn(LocalDateTime.now());
        userToDelete.setStatus(UserStatus.INACTIVE);
    }

    @Transactional
    public void changePassword(UUID id, ChangePasswordDto request) {
        if (request.newPassword().contains(request.oldPassword())) {
            throw new PasswordsAreEquals("A nova senha não pode conter informações da antiga.");
        }
        String newPasswordEncrypted = encryptPassword(request.newPassword());
        UserEntity user = userRepository.findById(id).orElseThrow();
        user.setPassword(newPasswordEncrypted);
        user.setModifiedOn(LocalDateTime.now());
    }

    @Transactional
    public boolean changePassword(ForgotPasswordDto request) {
        if (request.account().isEmpty()) {
            return false;
        }

        UserEntity user;
        user = userRepository.findUserByEmailAndIsDeletedFalse(request.account());
        if (user == null) {
            user = userRepository.findUserByUsernameAndIsDeletedFalse(request.account());

            if (user == null) throw new UserNotFound("Usuário não encontrado");
        }

        var newGeneratedPassword = passwordGenerator.generate();
        user.setModifiedOn(LocalDateTime.now());
        user.setStatus(UserStatus.FORCE_CHANGE_PASSWORD);
        user.setPassword(encryptPassword(newGeneratedPassword));

        System.out.println("\n\nSENHA GERADA: " + newGeneratedPassword + "\n\n");
        // TODO: Disparar email para o usuário que redefiniu a senha informando qual foi o resultado.
        return true;
    }

    public static String encryptPassword(String rawPassword) {
        return new BCryptPasswordEncoder().encode(rawPassword);
    }

    public static String createUserName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("O nome não pode ser vazio.");
        }

        String normalized = Normalizer.normalize(fullName, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-zA-Z\\s]", "")
                .trim()
                .toLowerCase();

        String[] names = normalized.split("\\s+");
        int random = ThreadLocalRandom.current().nextInt(10000);

        if (names.length <= 1)
            return String.format("%s#%04d", names[0], random);

        String firstName = names[0];
        String lastName = names[names.length - 1];

        return String.format("%s.%s#%04d", firstName, lastName, random);
    }
}
