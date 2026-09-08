package com.routeapp.routebackend.auth.entity;

import com.routeapp.routebackend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "refresh_tokens",
        uniqueConstraints = @UniqueConstraint(name = "uk_refresh_tokens_hash", columnNames = "token_hash"),
        indexes = {
                @Index(name = "idx_refresh_tokens_principal", columnList = "principal_type, principal_id"),
                @Index(name = "idx_refresh_tokens_expires_at", columnList = "expires_at")
        }
)
public class RefreshToken extends BaseEntity {

    // principalId, hem users.id hem admins.id'ye işaret edebilir — polimorfik,
    @Column(name = "principal_id", nullable = false)
    private UUID principalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "principal_type", nullable = false, length = 20)
    private PrincipalType principalType;

    // Ham token asla saklanmıyor — SHA-256 hash'i saklanıyor.
    @Column(name = "token_hash", nullable = false, length = 255)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    public boolean isValid() {
        return revokedAt == null && Instant.now().isBefore(expiresAt);
    }

    public void revoke() {
        if (this.revokedAt == null) {
            this.revokedAt = Instant.now();
        }
    }
}