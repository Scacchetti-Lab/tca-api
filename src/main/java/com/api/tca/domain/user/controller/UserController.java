package com.api.tca.domain.user.controller;

import com.api.tca.common.model.ApiResponse;
import com.api.tca.domain.user.dto.RegisterRequestDto;
import com.api.tca.domain.user.dto.RegisterResponseDto;
import com.api.tca.domain.user.repository.UserRepository;
import com.api.tca.domain.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<RegisterResponseDto>> registerNewUser(@RequestBody @Valid RegisterRequestDto request) {
        var newUserDto = userService.registerUser(request);
        var response = new ApiResponse<>("201", "Usuário criado com sucesso", newUserDto, true);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
