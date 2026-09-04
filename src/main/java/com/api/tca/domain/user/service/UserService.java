package com.api.tca.domain.user.service;

import com.api.tca.common.model.ApiResponse;
import com.api.tca.domain.address.entity.AddressEntity;
import com.api.tca.domain.address.service.AddressService;
import com.api.tca.domain.user.dto.user.ChangePasswordDto;
import com.api.tca.domain.user.dto.user.RegisterRequestDto;
import com.api.tca.domain.user.dto.user.RegisterResponseDto;
import com.api.tca.domain.user.entity.UserEntity;
import com.api.tca.domain.user.exception.PasswordsAreEquals;
import com.api.tca.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private AddressService addressService;

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
    public void changePassword(UUID id, ChangePasswordDto request) {
        if (request.newPassword().contains(request.oldPassword())) {
            throw new PasswordsAreEquals("A nova senha não pode conter informações da antiga.");
        }
        String newPasswordEncrypted = encryptPassword(request.newPassword());
        UserEntity user = userRepository.findById(id).orElseThrow();
        user.setPassword(newPasswordEncrypted);
        user.setModifiedOn(LocalDateTime.now());
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
