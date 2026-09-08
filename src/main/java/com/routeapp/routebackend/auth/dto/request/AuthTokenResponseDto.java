package com.routeapp.routebackend.auth.dto.response;

import lombok.Builder;

@Builder
public record AuthTokenResponseDto(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInSeconds
) {
    public static AuthTokenResponseDto of(String accessToken, String refreshToken, long expiresInSeconds) {
        return AuthTokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresInSeconds(expiresInSeconds)
                .build();
    }
}