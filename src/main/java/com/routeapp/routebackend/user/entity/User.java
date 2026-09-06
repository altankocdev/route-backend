package com.routeapp.routebackend.user.entity;

import com.routeapp.routebackend.common.entity.BaseEntity;
import com.routeapp.routebackend.common.exception.BusinessException;
import com.routeapp.routebackend.common.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_users_google_id", columnNames = "google_id"),
                @UniqueConstraint(name = "uk_users_username", columnNames = "username")
        },
        indexes = {
                @Index(name = "idx_users_status", columnList = "status"),
                @Index(name = "idx_users_role", columnList = "role")
        }
)
public class User extends BaseEntity {

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "username", nullable = false, length = 30)
    private String username;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "google_id", length = 255)
    private String googleId;

    @Column(name = "profile_photo_url", length = 500)
    private String profilePhotoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    @Builder.Default
    private UserRole role = UserRole.USER;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "ban_reason", length = 500)
    private String banReason;

    @Column(name = "banned_at")
    private Instant bannedAt;

    @Column(name = "email_verified", nullable = false)
    @Builder.Default
    private boolean emailVerified = false;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "terms_accepted_at")
    private Instant termsAcceptedAt;

    @Column(name = "deletion_requested_at")
    private Instant deletionRequestedAt;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isGoogleAccount() {
        return this.googleId != null;
    }

    public void updateName(String firstName, String lastName) {
        if (firstName == null || firstName.isBlank()) {
            throw new BusinessException(ErrorCode.USER_FIRST_NAME_REQUIRED);
        }
        if (lastName == null || lastName.isBlank()) {
            throw new BusinessException(ErrorCode.USER_LAST_NAME_REQUIRED);
        }
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * DİKKAT: Bu metod, servis katmanında SADECE yeni e-posta token ile
     * doğrulandıktan SONRA çağrılmalıdır (bkz. UserToken / EMAIL_CHANGE akışı).
     * Bu yüzden email değişirken emailVerified de otomatik true'ya çekiliyor —
     * çünkü bu noktaya gelinmesi zaten sahipliğin kanıtlandığı anlamına geliyor.
     */
    public void changeEmail(String newEmail) {
        if (newEmail == null || newEmail.isBlank()) {
            throw new BusinessException(ErrorCode.USER_EMAIL_REQUIRED);
        }
        this.email = newEmail;
        this.emailVerified = true;
    }

    public void changeUsername(String newUsername) {
        if (newUsername == null || newUsername.isBlank()) {
            throw new BusinessException(ErrorCode.USER_USERNAME_REQUIRED);
        }
        this.username = newUsername;
    }

    public void changePasswordHash(String newPasswordHash) {
        if (newPasswordHash == null || newPasswordHash.isBlank()) {
            throw new BusinessException(ErrorCode.USER_PASSWORD_REQUIRED);
        }
        this.passwordHash = newPasswordHash;
    }

    public void changeProfilePhotoUrl(String url) {
        this.profilePhotoUrl = url;
    }

    public void ban(String reason) {
        if (this.status == UserStatus.BANNED) {
            return;
        }
        this.status = UserStatus.BANNED;
        this.banReason = reason;
        this.bannedAt = Instant.now();
    }

    public void unban() {
        if (this.status != UserStatus.BANNED) {
            return;
        }
        this.status = UserStatus.ACTIVE;
        this.banReason = null;
        this.bannedAt = null;
    }

    public void deactivateAccount() {
        if (this.status == UserStatus.DEACTIVATED) {
            return;
        }
        this.status = UserStatus.DEACTIVATED;
    }

    public void reactivateAccount() {
        if (this.status == UserStatus.ACTIVE) {
            return;
        }
        this.status = UserStatus.ACTIVE;
    }

    public void markEmailAsVerified() {
        this.emailVerified = true;
    }

    public void recordLogin() {
        this.lastLoginAt = Instant.now();
    }

    public void acceptTerms() {
        this.termsAcceptedAt = Instant.now();
    }

    /**
     * UC-11: Kullanıcı kendi hesabını sildiğinde çağrılır.
     * status = DELETED, ama satır 30 gün boyunca DB'de kalır (grace period).
     */
    public void requestAccountDeletion() {
        if (this.deletionRequestedAt != null) {
            return;
        }
        this.deletionRequestedAt = Instant.now();
        this.status = UserStatus.DELETED;
    }

    /**
     * 30 gün dolmadan kullanıcı tekrar giriş yaparsa servis katmanı bunu çağırır.
     */
    public void cancelAccountDeletion() {
        if (this.deletionRequestedAt == null) {
            return;
        }
        this.deletionRequestedAt = null;
        this.status = UserStatus.ACTIVE;
    }
}