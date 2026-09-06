package com.routeapp.routebackend.user.entity.token;

import com.routeapp.routebackend.common.entity.BaseEntity;
import com.routeapp.routebackend.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "user_tokens",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_tokens_token", columnNames = "token"),
        indexes = {
                @Index(name = "idx_user_tokens_user_id", columnList = "user_id"),
                @Index(name = "idx_user_tokens_expires_at", columnList = "expires_at")
        }
)
public class UserToken extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token", nullable = false, length = 255)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false, length = 30)
    private TokenType tokenType;

    /**
     * Sadece EMAIL_CHANGE tipinde dolu — onaylanacak yeni e-posta burada bekletilir,
     * kullanıcının asıl 'email' alanı token doğrulanana kadar değişmez.
     */
    @Column(name = "new_email", length = 150)
    private String newEmail;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "used_at")
    private Instant usedAt;

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiresAt);
    }

    public boolean isUsed() {
        return this.usedAt != null;
    }

    public boolean isValid() {
        return !isExpired() && !isUsed();
    }

    public void markAsUsed() {
        this.usedAt = Instant.now();
    }
}