package com.api.tca.domain.user.controller;

import com.api.tca.domain.user.dto.AuthenticateDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticateController {

    @PostMapping
    public ResponseEntity authenticate(@RequestBody @Valid AuthenticateDto data) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Endpoint de Login em construção...");
        /*
        var token = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var authentication = manager.authenticate(token);

        return ResponseEntity.ok(token);
         */
    }
}
