package com.routeapp.routebackend.common.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AuditorAwareImpl implements AuditorAware<UUID> {

    @Override
    public Optional<UUID> getCurrentAuditor() {
        // TODO: Spring Security eklendiğinde SecurityContextHolder'dan
        // giriş yapmış kullanıcının UUID'sini çekecek şekilde güncellenecek.
        // Örn: SecurityContextHolder.getContext().getAuthentication()...
        return Optional.empty();
    }
}