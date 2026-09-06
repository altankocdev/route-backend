package com.routeapp.routebackend.user.dto.response;

import com.routeapp.routebackend.user.entity.User;
import com.routeapp.routebackend.user.entity.UserRole;
import com.routeapp.routebackend.user.entity.UserStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

/**
 * Kullanıcının KENDİ profilini görüntülerken kullanılan tam DTO.
 * Hassas alanları (passwordHash, googleId) hiç içermez.
 */
@Builder
public record UserResponseDto(
        UUID id,
        String username,
        String firstName,
        String lastName,
        String fullName,
        String email,
        boolean emailVerified,
        String profilePhotoUrl,
        UserRole role,
        UserStatus status,
        Instant createdAt
) {
    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .emailVerified(user.isEmailVerified())
                .profilePhotoUrl(user.getProfilePhotoUrl())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}