package com.api.tca.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ApiResponse<T> {
    private String statusCode;
    private String message;
    private T content;
    private boolean isValid;


    public static ApiResponse<?> Invalid(String statusCode, String message) {
        return new ApiResponse<>(statusCode, message, null, false);
    }

    public static ApiResponse<?> Valid(String statusCode, String message) {
        return new ApiResponse<>(statusCode, message, null, true);
    }

    public ApiResponse<T> ValidGet(T content) {
        return new ApiResponse<>("200", "Ação concluída", content, true);
    }

    public ApiResponse<T> ValidPost(T content) {
        return new ApiResponse<>("201", "Ação concluída", content, true);
    }

    public ApiResponse<T> Valid(String statusCode, String message, T content) {
        return new ApiResponse<>(statusCode, message, content, true);
    }
}
