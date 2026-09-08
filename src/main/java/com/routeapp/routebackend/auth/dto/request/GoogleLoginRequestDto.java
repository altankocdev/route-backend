package com.routeapp.routebackend.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record GoogleLoginRequestDto(
        @NotBlank(message = "{validation.googleIdToken.required}")
        String idToken
) {}