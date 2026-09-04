package com.api.tca.domain.user.controller;

import com.api.tca.common.model.ApiResponse;
import com.api.tca.common.model.SuccessResult;
import com.api.tca.domain.user.dto.user.ChangePasswordDto;
import com.api.tca.domain.user.dto.user.RegisterRequestDto;
import com.api.tca.domain.user.dto.user.RegisterResponseDto;
import com.api.tca.domain.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Registra novo usuário no sistema
     * */
    @PostMapping
    public ResponseEntity<ApiResponse<RegisterResponseDto>> registerNewUser(@RequestBody @Valid RegisterRequestDto request) {
        var newUserDto = userService.registerUser(request);
        var response = new ApiResponse<>("201", "Usuário criado com sucesso", newUserDto, true);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Modifica a senha do usuário
     * */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> changePassword(@PathVariable UUID id, @RequestBody @Valid ChangePasswordDto request) {
        userService.changePassword(id, request);

        return ResponseEntity.ok().body(new SuccessResult<>("200", "Senha modificada com sucesso!"));
    }
}
