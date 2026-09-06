package com.routeapp.routebackend.user.dto.request.verification;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record VerifyEmailRequestDto(
        @NotBlank(message = "{validation.token.required}")
        String token
) {}