package com.routeapp.routebackend.admin.dto.response;

import com.routeapp.routebackend.admin.entity.Admin;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record AdminResponseDto(
        UUID id,
        String fullName,
        String email,
        Instant createdAt
) {
    public static AdminResponseDto from(Admin admin) {
        return AdminResponseDto.builder()
                .id(admin.getId())
                .fullName(admin.getFullName())
                .email(admin.getEmail())
                .createdAt(admin.getCreatedAt())
                .build();
    }
}