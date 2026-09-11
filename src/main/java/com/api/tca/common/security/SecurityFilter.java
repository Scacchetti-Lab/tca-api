package com.api.tca.common.security;

import com.api.tca.domain.user.dto.auth.TokenRegisterDto;
import com.api.tca.domain.user.exception.UserNotFound;
import com.api.tca.domain.user.repository.UserRepository;
import com.api.tca.domain.user.security.UserSecurity;
import com.api.tca.domain.user.service.TokenService;
import com.api.tca.domain.user.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var tokenJwt = getToken(request);
        if (tokenJwt != null) {
            TokenRegisterDto subject = tokenService.getSubject(tokenJwt);
            var userEntity = repository.findByIsDeletedFalseAndUsernameOrIsDeletedFalseAndEmail(subject.username(), subject.email())
                    .orElseThrow(() -> new UserNotFound("Usuário do token é inválido."));
            var user = new UserSecurity(userEntity);

            var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}

