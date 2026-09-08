package com.routeapp.routebackend.auth.security;

import com.routeapp.routebackend.auth.entity.PrincipalType;

import java.util.UUID;

// SecurityContext'e konan, her request'te JWT filter tarafından kurulan hafif nesne.
// DB'den YENİDEN OKUMA yapılmıyor — sadece token içeriğinden üretiliyor (performans).
public record AuthenticatedPrincipal(UUID id, PrincipalType type) {
}