package com.routeapp.routebackend.user.dto.request.registration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RegisterRequestDto(
        @NotBlank(message = "{validation.firstName.required}")
        @Size(max = 100, message = "{validation.firstName.tooLong}")
        String firstName,

        @NotBlank(message = "{validation.lastName.required}")
        @Size(max = 100, message = "{validation.lastName.tooLong}")
        String lastName,

        @NotBlank(message = "{validation.username.required}")
        @Size(min = 3, max = 30, message = "{validation.username.size}")
        @Pattern(regexp = "^[a-z0-9_]+$", message = "{validation.username.pattern}")
        String username,

        @NotBlank(message = "{validation.email.required}")
        @Email(message = "{validation.email.invalid}")
        @Size(max = 150, message = "{validation.email.tooLong}")
        String email,

        @NotBlank(message = "{validation.password.required}")
        @Size(min = 8, max = 100, message = "{validation.password.tooShort}")
        String password
) {}