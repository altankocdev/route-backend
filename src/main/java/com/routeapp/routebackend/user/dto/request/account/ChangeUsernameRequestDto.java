package com.routeapp.routebackend.user.dto.request.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ChangeUsernameRequestDto(
        @NotBlank(message = "{validation.username.required}")
        @Size(min = 3, max = 30, message = "{validation.username.size}")
        @Pattern(regexp = "^[a-z0-9_]+$", message = "{validation.username.pattern}")
        String newUsername
) {}