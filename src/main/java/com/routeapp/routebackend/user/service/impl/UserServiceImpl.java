package com.routeapp.routebackend.user.service.impl;

import com.routeapp.routebackend.activitylog.entity.LogActorType;
import com.routeapp.routebackend.activitylog.entity.LogEventType;
import com.routeapp.routebackend.activitylog.service.ActivityLogService;
import com.routeapp.routebackend.common.enums.TargetType;
import com.routeapp.routebackend.common.exception.BusinessException;
import com.routeapp.routebackend.common.exception.ErrorCode;
import com.routeapp.routebackend.user.dto.request.account.ChangeEmailRequestDto;
import com.routeapp.routebackend.user.dto.request.account.ChangePasswordRequestDto;
import com.routeapp.routebackend.user.dto.request.account.ChangeUsernameRequestDto;
import com.routeapp.routebackend.user.dto.request.account.UpdateProfileRequestDto;
import com.routeapp.routebackend.user.dto.request.registration.RegisterRequestDto;
import com.routeapp.routebackend.user.dto.request.verification.ForgotPasswordRequestDto;
import com.routeapp.routebackend.user.dto.request.verification.ResetPasswordRequestDto;
import com.routeapp.routebackend.user.dto.response.UserResponseDto;
import com.routeapp.routebackend.user.entity.User;
import com.routeapp.routebackend.user.entity.UserStatus;
import com.routeapp.routebackend.user.entity.token.TokenType;
import com.routeapp.routebackend.user.entity.token.UserToken;
import com.routeapp.routebackend.user.mapper.UserMapper;
import com.routeapp.routebackend.user.repository.UserRepository;
import com.routeapp.routebackend.user.repository.UserTokenRepository;
import com.routeapp.routebackend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private static final int VERIFICATION_TOKEN_TTL_HOURS = 24;
    private static final int PASSWORD_RESET_TOKEN_TTL_HOURS = 1;

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ActivityLogService activityLogService;

    @Override
    public User getByIdOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public UserResponseDto getOwnProfile(UUID userId) {
        return userMapper.toResponseDto(getByIdOrThrow(userId));
    }

    @Override
    public Page<User> searchUsers(String query, Pageable pageable) {
        return userRepository.searchActiveUsers(query, pageable);
    }

    @Override
    public Page<User> getActiveUsers(Pageable pageable) {
        return userRepository.findByStatus(UserStatus.ACTIVE, pageable);
    }

    @Override
    public void requireVerifiedEmail(User user) {
        if (!user.isEmailVerified()) {
            throw new BusinessException(ErrorCode.EMAIL_NOT_VERIFIED);
        }
    }

    @Override
    @Transactional
    public UserResponseDto register(RegisterRequestDto dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (userRepository.existsByUsername(dto.username())) {
            throw new BusinessException(ErrorCode.USER_USERNAME_ALREADY_EXISTS);
        }

        String passwordHash = passwordEncoder.encode(dto.password());
        User user = User.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .username(dto.username())
                .email(dto.email())
                .passwordHash(passwordHash)
                .build();
        user = userRepository.save(user);

        issueToken(user, TokenType.EMAIL_VERIFICATION, null, VERIFICATION_TOKEN_TTL_HOURS);
        logUserEvent(user, LogEventType.USER_REGISTERED, null);
        // TODO: EmailService hazır olunca doğrulama linkini gönder

        return userMapper.toResponseDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto findOrCreateGoogleUser(String googleId, String firstName, String lastName,
                                                  String email, String profilePhotoUrl) {
        return userRepository.findByGoogleId(googleId)
                .map(existing -> {
                    existing.recordLogin();
                    logUserEvent(existing, LogEventType.USER_LOGGED_IN, "Google");
                    return userMapper.toResponseDto(existing);
                })
                .orElseGet(() -> {
                    if (userRepository.existsByEmail(email)) {
                        throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
                    }
                    User user = User.builder()
                            .firstName(firstName)
                            .lastName(lastName)
                            .username(generateUniqueTemporaryUsername())
                            .email(email)
                            .googleId(googleId)
                            .profilePhotoUrl(profilePhotoUrl)
                            .build();
                    user.markEmailAsVerified();
                    user.recordLogin();
                    user = userRepository.save(user);
                    logUserEvent(user, LogEventType.USER_REGISTERED, "Google");
                    return userMapper.toResponseDto(user);
                });
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        UserToken userToken = getValidTokenOrThrow(token, TokenType.EMAIL_VERIFICATION);
        User user = userToken.getUser();
        if (user.isEmailVerified()) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_VERIFIED);
        }
        user.markEmailAsVerified();
        userToken.markAsUsed();
        logUserEvent(user, LogEventType.EMAIL_VERIFIED, null);
    }

    @Override
    @Transactional
    public void resendVerificationEmail(UUID userId) {
        User user = getByIdOrThrow(userId);
        if (user.isEmailVerified()) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_VERIFIED);
        }
        issueToken(user, TokenType.EMAIL_VERIFICATION, null, VERIFICATION_TOKEN_TTL_HOURS);
        // TODO: EmailService hazır olunca yeni linki gönder
    }

    @Override
    @Transactional
    public UserResponseDto updateProfile(UUID userId, UpdateProfileRequestDto dto) {
        User user = getByIdOrThrow(userId);
        user.updateName(dto.firstName(), dto.lastName());
        user.changeProfilePhotoUrl(dto.profilePhotoUrl());
        return userMapper.toResponseDto(user);
    }

    @Override
    @Transactional
    public void requestEmailChange(UUID userId, ChangeEmailRequestDto dto) {
        User user = getByIdOrThrow(userId);
        if (user.isGoogleAccount()) {
            throw new BusinessException(ErrorCode.BUSINESS_RULE_VIOLATION);
        }
        if (!passwordEncoder.matches(dto.currentPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
        if (userRepository.existsByEmailAndIdNot(dto.newEmail(), userId)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        issueToken(user, TokenType.EMAIL_CHANGE, dto.newEmail(), VERIFICATION_TOKEN_TTL_HOURS);
        logUserEvent(user, LogEventType.EMAIL_CHANGE_REQUESTED, null);
        // TODO: EmailService hazır olunca linki YENİ e-postaya gönder
    }

    @Override
    @Transactional
    public void confirmEmailChange(String token) {
        UserToken userToken = getValidTokenOrThrow(token, TokenType.EMAIL_CHANGE);
        User user = userToken.getUser();
        user.changeEmail(userToken.getNewEmail());
        userToken.markAsUsed();
        logUserEvent(user, LogEventType.EMAIL_CHANGED, null);
    }

    @Override
    @Transactional
    public void changeUsername(UUID userId, ChangeUsernameRequestDto dto) {
        User user = getByIdOrThrow(userId);
        if (userRepository.existsByUsernameAndIdNot(dto.newUsername(), userId)) {
            throw new BusinessException(ErrorCode.USER_USERNAME_ALREADY_EXISTS);
        }
        user.changeUsername(dto.newUsername());
        logUserEvent(user, LogEventType.USERNAME_CHANGED, null);
    }

    @Override
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequestDto dto) {
        User user = getByIdOrThrow(userId);
        if (user.isGoogleAccount()) {
            throw new BusinessException(ErrorCode.BUSINESS_RULE_VIOLATION);
        }
        if (!passwordEncoder.matches(dto.currentPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
        user.changePasswordHash(passwordEncoder.encode(dto.newPassword()));
        logUserEvent(user, LogEventType.PASSWORD_CHANGED, null);
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequestDto dto) {
        userRepository.findByEmail(dto.email()).ifPresent(user -> {
            if (!user.isGoogleAccount()) {
                issueToken(user, TokenType.PASSWORD_RESET, null, PASSWORD_RESET_TOKEN_TTL_HOURS);
                logUserEvent(user, LogEventType.PASSWORD_RESET_REQUESTED, null);
                // TODO: EmailService hazır olunca linki gönder
            }
        });
        // Kayıtlı olmayan e-posta için de sessizce başarı dönülür — enumeration riskini önlemek için
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequestDto dto) {
        UserToken userToken = getValidTokenOrThrow(dto.token(), TokenType.PASSWORD_RESET);
        User user = userToken.getUser();
        user.changePasswordHash(passwordEncoder.encode(dto.newPassword()));
        userToken.markAsUsed();
        logUserEvent(user, LogEventType.PASSWORD_RESET_COMPLETED, null);
    }

    @Override
    @Transactional
    public void deactivateAccount(UUID userId) {
        User user = getByIdOrThrow(userId);
        user.deactivateAccount();
        logUserEvent(user, LogEventType.ACCOUNT_DEACTIVATED, null);
    }

    @Override
    @Transactional
    public void reactivateAccount(UUID userId) {
        User user = getByIdOrThrow(userId);
        user.reactivateAccount();
        logUserEvent(user, LogEventType.ACCOUNT_REACTIVATED, null);
    }

    @Override
    @Transactional
    public void requestAccountDeletion(UUID userId) {
        User user = getByIdOrThrow(userId);
        user.requestAccountDeletion();
        logUserEvent(user, LogEventType.ACCOUNT_DELETION_REQUESTED, null);
    }

    @Override
    @Transactional
    public void cancelAccountDeletion(UUID userId) {
        User user = getByIdOrThrow(userId);
        user.cancelAccountDeletion();
        logUserEvent(user, LogEventType.ACCOUNT_DELETION_CANCELLED, null);
    }

    @Override
    @Transactional
    public void recordSuccessfulLogin(UUID userId) {
        User user = getByIdOrThrow(userId);
        if (user.getStatus() == UserStatus.BANNED) {
            throw new BusinessException(ErrorCode.USER_INACTIVE);
        }
        if (user.getStatus() == UserStatus.DELETED) {
            user.cancelAccountDeletion();
            logUserEvent(user, LogEventType.ACCOUNT_DELETION_CANCELLED, "Girişle otomatik iptal");
        }
        user.recordLogin();
        logUserEvent(user, LogEventType.USER_LOGGED_IN, null);
    }

    private void logUserEvent(User user, LogEventType eventType, String detail) {
        activityLogService.log(user.getId(), LogActorType.USER, eventType,
                TargetType.USER, user.getId(), detail);
    }

    private String generateUniqueTemporaryUsername() {
        String candidate;
        do {
            candidate = "user_" + UUID.randomUUID().toString().substring(0, 8);
        } while (userRepository.existsByUsername(candidate));
        return candidate;
    }

    private UserToken getValidTokenOrThrow(String rawToken, TokenType type) {
        UserToken token = userTokenRepository.findByTokenAndTokenType(rawToken, type)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_OR_EXPIRED_TOKEN));
        if (!token.isValid()) {
            throw new BusinessException(ErrorCode.INVALID_OR_EXPIRED_TOKEN);
        }
        return token;
    }

    private void issueToken(User user, TokenType type, String newEmail, int ttlHours) {
        UserToken token = UserToken.builder()
                .user(user)
                .token(generateSecureToken())
                .tokenType(type)
                .newEmail(newEmail)
                .expiresAt(Instant.now().plus(ttlHours, ChronoUnit.HOURS))
                .build();
        userTokenRepository.save(token);
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}