package com.routeapp.routebackend.user.dto.response;

import com.routeapp.routebackend.user.entity.User;
import lombok.Builder;

import java.util.UUID;

/**
 * Başka bir kaynağın (Location, Activity) "sahibi" olarak gömülü şekilde
 * gösterilen, minimal ve HERKESE AÇIK kullanıcı görünümü.
 * Email gibi hassas bilgi kesinlikle içermez.
 */
@Builder
public record UserSummaryResponseDto(
        UUID id,
        String username,
        String displayName,
        String profilePhotoUrl
) {
    public static UserSummaryResponseDto fromActive(User user) {
        return UserSummaryResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .displayName(user.getFullName())
                .profilePhotoUrl(user.getProfilePhotoUrl())
                .build();
    }

    public static UserSummaryResponseDto ofDeletedUser(UUID id, String deletedDisplayName) {
        return UserSummaryResponseDto.builder()
                .id(id)
                .username(null)
                .displayName(deletedDisplayName)
                .profilePhotoUrl(null)
                .build();
    }
}