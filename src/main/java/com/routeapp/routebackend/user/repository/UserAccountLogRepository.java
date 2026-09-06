package com.routeapp.routebackend.user.repository;

import com.routeapp.routebackend.user.entity.accountlog.UserAccountLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserAccountLogRepository extends JpaRepository<UserAccountLog, UUID> {
    // Aynı metod hem "kendi geçmişim" hem "admin, X kullanıcısının geçmişi" için kullanılıyor —
    // kim çağırabilir sorusu controller/security katmanında ayrılıyor, repository'de değil.
    Page<UserAccountLog> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}