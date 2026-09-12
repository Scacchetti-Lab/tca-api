package com.api.tca.domain.user.controller;

import com.api.tca.common.model.ApiResponse;
import com.api.tca.common.model.SuccessResult;
import com.api.tca.domain.user.dto.auth.AuthResponseDto;
import com.api.tca.domain.user.dto.auth.AuthenticateDto;
import com.api.tca.domain.user.entity.UserEntity;
import com.api.tca.domain.user.security.UserSecurity;
import com.api.tca.domain.user.service.TokenService;
import com.api.tca.domain.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.attribute.UserPrincipalNotFoundException;

@RestController
@RequestMapping("/auth")
public class AuthenticateController {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<AuthResponseDto>> authenticate(@RequestBody @Valid AuthenticateDto data)  {
        var responseDto = userService.authenticate(manager, data);
        return ResponseEntity.ok(new SuccessResult<>("Usuário Autenticado", responseDto));
    }
}
