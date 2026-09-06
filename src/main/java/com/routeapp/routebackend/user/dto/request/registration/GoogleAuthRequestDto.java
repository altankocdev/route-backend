package com.routeapp.routebackend.user.dto.request.registration;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * Hem yeni kayıt hem mevcut kullanıcı girişi için kullanılır.
 * Backend, idToken'ı Google'ın sunucu tarafı kütüphanesiyle doğrulayıp
 * email/isim/fotoğraf bilgisini DOĞRULANMIŞ TOKEN İÇİNDEN çıkarır.
 */
@Builder
public record GoogleAuthRequestDto(
        @NotBlank(message = "{validation.googleIdToken.required}")
        String idToken
) {}