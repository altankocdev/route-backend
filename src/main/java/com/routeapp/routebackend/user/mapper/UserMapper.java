package com.routeapp.routebackend.user.mapper;

import com.routeapp.routebackend.user.dto.request.registration.RegisterRequestDto;
import com.routeapp.routebackend.user.dto.response.UserResponseDto;
import com.routeapp.routebackend.user.dto.response.UserSummaryResponseDto;
import com.routeapp.routebackend.user.entity.User;
import com.routeapp.routebackend.user.entity.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final MessageSource messageSource;

    public UserResponseDto toResponseDto(User user) {
        return UserResponseDto.from(user);
    }

    public UserSummaryResponseDto toSummaryDto(User owner, Locale locale) {
        if (owner.getStatus() == UserStatus.ACTIVE) {
            return UserSummaryResponseDto.fromActive(owner);
        }
        String deletedDisplayName = messageSource.getMessage("user.deletedDisplayName", null, locale);
        return UserSummaryResponseDto.ofDeletedUser(owner.getId(), deletedDisplayName);
    }

    /**
     * DİKKAT: passwordHash burada ZATEN HASH'LENMİŞ olarak gelmeli.
     * Şifreyi hash'leme işi (PasswordEncoder ile) servis katmanının
     * sorumluluğu — mapper'a asla ham şifre geçirilmemeli.
     */
    public User toEntity(RegisterRequestDto dto, String passwordHash) {
        return User.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .username(dto.username())
                .email(dto.email())
                .passwordHash(passwordHash)
                .build();
        // role, status, emailVerified: User entity'sindeki @Builder.Default
        // değerleri (USER, ACTIVE, false) otomatik uygulanıyor — burada
        // tekrar belirtmemize gerek yok.
    }
}