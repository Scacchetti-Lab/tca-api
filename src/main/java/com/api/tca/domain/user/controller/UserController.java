package com.api.tca.domain.user.controller;

import com.api.tca.common.model.ApiResponse;
import com.api.tca.common.model.FailureResult;
import com.api.tca.common.model.SuccessResult;
import com.api.tca.domain.user.dto.user.*;
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
     * @param id {@code UUID} Identificador único do usuário
     * @param request {@Code ChangePasswordDto} DTO para troca de senha
     * */
    @PatchMapping("/{id}/reset-password")
    public ResponseEntity<ApiResponse<String>> changePassword(@PathVariable UUID id, @RequestBody @Valid ChangePasswordDto request) {
        userService.changePassword(id, request);

        return ResponseEntity.ok().body(new SuccessResult<>("200", "Senha modificada com sucesso!"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody @Valid ForgotPasswordDto request) {
        var success = userService.changePassword(request);
        if (!success)
            return ResponseEntity.badRequest().body(new FailureResult<>("400", "Email ou Username devem ser informados"));
        return ResponseEntity.ok().body(new SuccessResult<>("200", "A sua senha foi redefinida, enviamos um email com novas informações!"));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(@PathVariable UUID id, @RequestBody @Valid UpdateUserDto request) {
        var response = userService.updateUser(id, request);
        return ResponseEntity.accepted().body(new SuccessResult<>("Usuário atualizado", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
