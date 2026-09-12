package com.api.tca.domain.user.service;

import com.api.tca.domain.user.dto.auth.TokenRegisterDto;
import com.api.tca.domain.user.entity.UserEntity;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.NoSuchElementException;

@Service
public class TokenService {

    @Value("${tca.security.token-secret}")
    private String secret;

    public String generateToken(UserEntity user) {
        try {
            var algorithm = Algorithm.HMAC256(secret);
            var jwtContent = String.format("%s|%s|%s", user.getEmail(), user.getUsername(), user.getFirstProfileName());
            return JWT.create()
                    .withIssuer("TCA api")
                    .withSubject(jwtContent)
                    .withExpiresAt(getExpireDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new RuntimeException("Erro ao gerar o token JWT", exception);
        }
        catch (NoSuchElementException ex) {
            throw new RuntimeException("Usuário incompleto, faltam dados para cadastro.");
        }
    }

    public TokenRegisterDto getSubject(String tokenJWT) {
        try {
            var algorithm = Algorithm.HMAC256(secret);
            String content = JWT.require(algorithm)
                    .withIssuer("TCA api")
                    .build()
                    .verify(getToken(tokenJWT))
                    .getSubject();
            String[] split = content.split("\\|");
            return new TokenRegisterDto(split[1], split[0], split[2]);

        } catch (JWTVerificationException exception){
            System.out.println(Arrays.toString(exception.getStackTrace()));
            throw new RuntimeException("Token JWT inválido ou expirado", exception);
        }
    }

    public TokenRegisterDto getAuthenticatedUser(HttpServletRequest httpRequest) {
        var token = httpRequest.getHeader("Authorization");
        return getSubject(token);
    }

    private String getToken(String tokenJWT) {
        return tokenJWT.replace("Bearer ", "");
    }

    private Instant getExpireDate() {
        return LocalDateTime.now().plusHours(3).toInstant(ZoneOffset.of("-03:00"));
    }
}
