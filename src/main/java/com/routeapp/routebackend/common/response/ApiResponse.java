package com.routeapp.routebackend.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String message,
        T data
) {
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder().
                success(true).
                data(data).
                build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder().
                success(true).
                message(message).
                data(data).
                build();
    }

    public static ApiResponse<Void> success(String message) {
        return ApiResponse.<Void>builder().
                success(true).
                message(message).
                build();
    }
}