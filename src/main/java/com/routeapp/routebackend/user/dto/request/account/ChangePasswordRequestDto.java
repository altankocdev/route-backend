package com.routeapp.routebackend.user.dto.request.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ChangePasswordRequestDto(
        @NotBlank(message = "{validation.currentPassword.required}")
        String currentPassword,

        @NotBlank(message = "{validation.password.required}")
        @Size(min = 8, max = 100, message = "{validation.password.tooShort}")
        String newPassword
) {}