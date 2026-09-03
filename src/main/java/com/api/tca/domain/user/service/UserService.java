package com.api.tca.domain.user.service;

import com.api.tca.domain.address.entity.AddressEntity;
import com.api.tca.domain.address.service.AddressService;
import com.api.tca.domain.user.dto.RegisterRequestDto;
import com.api.tca.domain.user.dto.RegisterResponseDto;
import com.api.tca.domain.user.entity.UserEntity;
import com.api.tca.domain.user.enums.ProfileTypes;
import com.api.tca.domain.user.enums.ScoreType;
import com.api.tca.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

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

    public static String encryptPassword(String rawPassword) {
        return new BCryptPasswordEncoder().encode(rawPassword);
    }

    public static String createUserName(String fullName) {
        Random rdn = new Random();
        var randomId = "#" + rdn.nextInt(99999) + 10000;

        var sliced = fullName.toLowerCase().split(" ");
        if (sliced.length > 0)
            return sliced[0] + "." + sliced[sliced.length-1] + randomId;
        return fullName + randomId;
    }

    public static ScoreType discoverScoreType(ProfileTypes profile) {
        return switch (profile) {
            case SALESPERSON -> ScoreType.PERFORMANCE;
            case DIRECTOR, MANAGER -> ScoreType.STRATEGIC;
        };
    }
}
