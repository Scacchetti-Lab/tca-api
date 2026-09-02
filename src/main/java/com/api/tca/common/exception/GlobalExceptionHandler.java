package com.api.tca.common.exception;

import com.api.tca.common.model.ApiResponse;
import com.api.tca.domain.address.exception.AddressNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AddressNotFound.class)
    public ResponseEntity<ApiResponse<?>> handleAddressNotFound(AddressNotFound ex) {
        var serverResponse = ApiResponse.Invalid("404", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(serverResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<FieldDataErrors>>> handleBadRequest(MethodArgumentNotValidException ex) {
        var errors = ex.getFieldErrors();
        var data = errors.stream().map(FieldDataErrors::new).toList();
        return ResponseEntity.badRequest().body(new ApiResponse<>("400", "Dados inválidos ou insuficientes", data, false));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public  ResponseEntity<ApiResponse<?>> handleBadCredentials() {
        var serverResponse = ApiResponse.Invalid("401", "Credenciais Inválidas");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(serverResponse);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthentication() {
        var serverResponse = ApiResponse.Invalid("401", "Falha ao se autenticar");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(serverResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public  ResponseEntity<ApiResponse<?>> handleAccessDenied() {
        var serverResponse = ApiResponse.Invalid("403", "Acesso Negado");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(serverResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleInternalServerError(Exception e) {
        var serverResponse = ApiResponse.Invalid("500", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(serverResponse);
    }

    private record FieldDataErrors(String field, String message) {
        public FieldDataErrors(FieldError fieldError) {
            this(fieldError.getField(), fieldError.getDefaultMessage());
        }
    }
}
