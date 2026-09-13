package com.api.tca.domain.user.service;

import com.api.tca.common.helpers.BrazilRealTime;
import com.api.tca.common.security.PasswordGenerator;
import com.api.tca.domain.address.entity.AddressEntity;
import com.api.tca.domain.address.service.AddressService;
import com.api.tca.domain.email.dto.email.EmailRequestDto;
import com.api.tca.domain.email.service.EmailService;
import com.api.tca.domain.user.dto.auth.AuthResponseDto;
import com.api.tca.domain.user.dto.auth.AuthenticateDto;
import com.api.tca.domain.user.dto.user.*;
import com.api.tca.domain.user.entity.UserEntity;
import com.api.tca.domain.user.enums.UserStatus;
import com.api.tca.domain.user.exception.PasswordsAreEquals;
import com.api.tca.domain.user.exception.UserNotFound;
import com.api.tca.domain.user.mapper.UserMapper;
import com.api.tca.domain.user.repository.UserRepository;
import com.api.tca.domain.user.security.UserSecurity;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;


@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private PasswordGenerator passwordGenerator;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserMapper mapper;

    public Page<UserResponseDto> findAllUsers(Pageable pageable) {
        return userRepository.findAllByIsDeletedFalse(pageable).map(UserResponseDto::new);
    }

    public AuthResponseDto authenticate(AuthenticationManager manager, AuthenticateDto data) {
        var token = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var authentication = manager.authenticate(token);
        var authUserPrincipal = (UserSecurity) authentication.getPrincipal();

        if (authUserPrincipal == null)
            throw new UserNotFound("Usuário principal de email não encontrado");

        var authUser = authUserPrincipal.getUserEntity();
        var authToken = tokenService.generateToken(authUserPrincipal.getUserEntity());
        return new AuthResponseDto(authUser.getEmail(), authUser.getFirstProfileName(), authToken);
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) {
        var user = userRepository.findUserByEmailAndIsDeletedFalse(username);
        if (user == null)
            throw new UsernameNotFoundException("Usuário não encontrado");

        return new UserSecurity(user);
    }

    public UserEntity getUserById(UUID id) {
        var userEntity = userRepository.findUserByIdAndIsDeletedFalse(id);
        if (userEntity == null)
            throw new UserNotFound("Usuário não encontrado");

        return userEntity;
    }

    public UserEntity getUserByEmail(String email) {
        var user = userRepository.findUserByEmailAndIsDeletedFalse(email);
        if (user == null)
            throw new UsernameNotFoundException("Usuário não encontrado");
        return user;
    }

    @Transactional
    public RegisterResponseDto registerUser(RegisterRequestDto request) {
        AddressEntity userAddress = addressService.findOrCreateAddressByPostalCode(request.address());

        var userProfile = profileService.getProfileByKey(request.profileType());
        var newUser = userRepository.save(new UserEntity(request, userProfile, userAddress));

        return new RegisterResponseDto(newUser);
    }

    @Transactional
    public UserResponseDto updateUser(UUID id, UpdateUserDto request) {
        var userDb = getUserById(id);
        mapper.mapUpdateDtoToUserEntity(request, userDb);
        userDb.setModifiedOn(BrazilRealTime.now());

        return new UserResponseDto(userDb);
    }

    @Transactional
    public void deleteUser(UUID id) {
        var userToDelete = getUserById(id);

        userToDelete.setDeleted(true);
        userToDelete.setModifiedOn(BrazilRealTime.now());
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
        user.setModifiedOn(BrazilRealTime.now());
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
        user.setModifiedOn(BrazilRealTime.now());
        user.setStatus(UserStatus.FORCE_CHANGE_PASSWORD);
        user.setPassword(encryptPassword(newGeneratedPassword));
        sendForgotPasswordEmail(user, newGeneratedPassword);
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

    private void sendForgotPasswordEmail(UserEntity user, String newPassword) {
        String subject = "Recuperação de acesso ao TOTVS AI";
        Map<String, String> bodyReplace = new HashMap<>();
        bodyReplace.put("{{nome_usuario}}", user.getFullName());
        bodyReplace.put("{{senha_temporaria}}", newPassword);
        bodyReplace.put("{{link_login}}", "https://tca.totvs.com.br/login");
        bodyReplace.put("{{tempo_expiracao}}", "7 dias");
        var hosts = new EmailRequestDto(null, user.getEmail());
        var username = user.getFullName();

        emailService.sendEmail(subject, bodyReplace, hosts, username);
    }
}
