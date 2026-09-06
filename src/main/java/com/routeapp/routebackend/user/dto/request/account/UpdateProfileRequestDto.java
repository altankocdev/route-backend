package com.routeapp.routebackend.user.dto.request.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateProfileRequestDto(
        @NotBlank(message = "{validation.firstName.required}")
        @Size(max = 100, message = "{validation.firstName.tooLong}")
        String firstName,

        @NotBlank(message = "{validation.lastName.required}")
        @Size(max = 100, message = "{validation.lastName.tooLong}")
        String lastName,

        String profilePhotoUrl
) {}