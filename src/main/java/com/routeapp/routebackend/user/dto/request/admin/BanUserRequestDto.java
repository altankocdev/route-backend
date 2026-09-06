package com.routeapp.routebackend.user.dto.request.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BanUserRequestDto(
        @NotBlank(message = "{validation.banReason.required}")
        String reason,

        @NotNull(message = "{validation.deleteContent.required}")
        Boolean deleteContent
) {}