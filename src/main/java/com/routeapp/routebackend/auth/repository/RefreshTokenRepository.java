package com.routeapp.routebackend.auth.repository;

import com.routeapp.routebackend.auth.entity.PrincipalType;
import com.routeapp.routebackend.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findByPrincipalIdAndPrincipalTypeAndRevokedAtIsNull(UUID principalId, PrincipalType principalType);

    // Banlanan/hesabı silinen bir kullanıcının TÜM oturumlarını anında düşürmek için —
    // AdminUserService.banUser yazıldığında buradan çağrılacak.
    @Modifying
    @Query("UPDATE RefreshToken r SET r.revokedAt = :now " +
            "WHERE r.principalId = :principalId AND r.principalType = :principalType AND r.revokedAt IS NULL")
    void revokeAllByPrincipal(@Param("principalId") UUID principalId,
                              @Param("principalType") PrincipalType principalType,
                              @Param("now") Instant now);
}