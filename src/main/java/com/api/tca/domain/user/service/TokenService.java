package com.api.tca.domain.user.service;

import com.api.tca.domain.user.dto.auth.AuthResponseDto;
import com.api.tca.domain.user.entity.UserEntity;
import com.api.tca.domain.user.security.UserSecurity;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${tca.security.token-secret}")
    private String secret;

    public String generateToken(UserEntity user) {
        try {
            var algorithm = Algorithm.HMAC256(secret);
            var jwtContent = String.format("%s|%s|%s", user.getEmail(), user.getUsername(), user.getFirstProfileName());
            return JWT.create()
                    .withIssuer("API Voll.med")
                    .withSubject(jwtContent)
                    .withExpiresAt(getExpireDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new RuntimeException("Erro ao gerar o token JWT", exception);
        }
    }

    private Instant getExpireDate() {
        return LocalDateTime.now().plusHours(3).toInstant(ZoneOffset.of("-03:00"));
    }
}
