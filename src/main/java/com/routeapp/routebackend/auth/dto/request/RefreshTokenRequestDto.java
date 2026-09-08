package com.routeapp.routebackend.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RefreshTokenRequestDto(
        @NotBlank(message = "{validation.refreshToken.required}")
        String refreshToken
) {}