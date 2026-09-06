package com.routeapp.routebackend.user.dto.request.verification;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ForgotPasswordRequestDto(
        @NotBlank(message = "{validation.email.required}")
        @Email(message = "{validation.email.invalid}")
        String email
) {}