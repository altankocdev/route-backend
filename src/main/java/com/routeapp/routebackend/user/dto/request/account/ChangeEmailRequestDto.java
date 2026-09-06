package com.routeapp.routebackend.user.dto.request.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ChangeEmailRequestDto(
        @NotBlank(message = "{validation.email.required}")
        @Email(message = "{validation.email.invalid}")
        @Size(max = 150, message = "{validation.email.tooLong}")
        String newEmail,

        @NotBlank(message = "{validation.currentPassword.required}")
        String currentPassword
) {}