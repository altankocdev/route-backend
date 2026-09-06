package com.routeapp.routebackend.user.dto.request.verification;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ConfirmEmailChangeRequestDto(
        @NotBlank(message = "{validation.token.required}")
        String token
) {}