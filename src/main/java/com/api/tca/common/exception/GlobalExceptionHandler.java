package com.api.tca.common.exception;

import com.api.tca.common.ai.dto.response.ResponseModelDto;
import com.api.tca.common.model.ApiResponse;
import com.api.tca.domain.address.exception.AddressNotFound;
import com.api.tca.domain.email.exception.EmailFailed;
import com.api.tca.domain.email.exception.TemplateNotFound;
import com.api.tca.domain.user.exception.PasswordsAreEquals;
import com.api.tca.domain.user.exception.ProfileNotFound;
import com.api.tca.domain.user.exception.UserNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ApiResponse<ResponseModelDto<?>>> handleHttpClientErrorException(HttpClientErrorException ex) {
        var serverResponse = new ApiResponse<ResponseModelDto<?>>(
                ex.getStatusCode().toString(), ex.getMessage(), ex.getResponseBodyAs(ResponseModelDto.class), false
        );
        return ResponseEntity.status(ex.getStatusCode()).body(serverResponse);
    }

    @ExceptionHandler({RuntimeException.class, NoSuchElementException.class})
    public ResponseEntity<ApiResponse<?>> handleNotFound(RuntimeException ex) {
        var serverResponse = ApiResponse.invalid("404", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(serverResponse);
    }

    @ExceptionHandler(EmailFailed.class)
    public ResponseEntity<ApiResponse<?>> handleEmailFailed(EmailFailed ex) {
        var serverResponse = ApiResponse.invalid("424", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(serverResponse);
    }

    @ExceptionHandler(PasswordsAreEquals.class)
    public ResponseEntity<ApiResponse<?>> handlePasswordAreEquals(PasswordsAreEquals ex) {
        var serverResponse = ApiResponse.invalid("400", ex.getMessage());
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
        var serverResponse = ApiResponse.invalid("401", "Credenciais Inválidas");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(serverResponse);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthentication() {
        var serverResponse = ApiResponse.invalid("401", "Falha ao se autenticar");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(serverResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public  ResponseEntity<ApiResponse<?>> handleAccessDenied() {
        var serverResponse = ApiResponse.invalid("403", "Acesso Negado");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(serverResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleInternalServerError(Exception e) {
        var serverResponse = ApiResponse.invalid("500", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(serverResponse);
    }

    private record FieldDataErrors(String field, String message) {
        public FieldDataErrors(FieldError fieldError) {
            this(fieldError.getField(), fieldError.getDefaultMessage());
        }
    }
}
