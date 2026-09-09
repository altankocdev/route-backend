package com.routeapp.routebackend.auth.service.impl;

import com.routeapp.routebackend.activitylog.entity.LogActorType;
import com.routeapp.routebackend.activitylog.entity.LogEventType;
import com.routeapp.routebackend.activitylog.service.ActivityLogService;
import com.routeapp.routebackend.admin.entity.Admin;
import com.routeapp.routebackend.admin.service.AdminService;
import com.routeapp.routebackend.auth.dto.request.AdminLoginRequestDto;
import com.routeapp.routebackend.auth.dto.request.GoogleLoginRequestDto;
import com.routeapp.routebackend.auth.dto.request.LoginRequestDto;
import com.routeapp.routebackend.auth.dto.request.RegisterRequestDto;
import com.routeapp.routebackend.auth.dto.response.AuthTokenResponseDto;
import com.routeapp.routebackend.auth.entity.PrincipalType;
import com.routeapp.routebackend.auth.entity.RefreshToken;
import com.routeapp.routebackend.auth.repository.RefreshTokenRepository;
import com.routeapp.routebackend.auth.security.JwtTokenProvider;
import com.routeapp.routebackend.auth.service.AuthService;
import com.routeapp.routebackend.auth.service.GoogleTokenVerifierService;
import com.routeapp.routebackend.auth.service.GoogleUserInfo;
import com.routeapp.routebackend.common.enums.TargetType;
import com.routeapp.routebackend.common.exception.BusinessException;
import com.routeapp.routebackend.common.exception.ErrorCode;
import com.routeapp.routebackend.common.util.SecureTokenGenerator;
import com.routeapp.routebackend.user.dto.response.UserResponseDto;
import com.routeapp.routebackend.user.entity.User;
import com.routeapp.routebackend.user.entity.UserStatus;
import com.routeapp.routebackend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    @Value("${app.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    private final UserService userService;
    private final AdminService adminService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final GoogleTokenVerifierService googleTokenVerifierService;
    private final ActivityLogService activityLogService;

    @Override
    @Transactional
    public AuthTokenResponseDto register(RegisterRequestDto dto) {
        UserResponseDto created = userService.register(dto);
        return issueTokenPair(created.id(), PrincipalType.USER);
    }

    @Override
    @Transactional
    public AuthTokenResponseDto login(LoginRequestDto dto) {
        User user = userService.findByEmailForLogin(dto.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        if (user.isGoogleAccount() || !passwordEncoder.matches(dto.password(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
        if (user.getStatus() == UserStatus.BANNED) {
            throw new BusinessException(ErrorCode.USER_INACTIVE);
        }

        userService.recordSuccessfulLogin(user.getId());
        return issueTokenPair(user.getId(), PrincipalType.USER);
    }

    @Override
    @Transactional
    public AuthTokenResponseDto googleLogin(GoogleLoginRequestDto dto) {
        GoogleUserInfo googleUser = googleTokenVerifierService.verify(dto.idToken());

        UserResponseDto user = userService.findOrCreateGoogleUser(
                googleUser.googleId(),
                googleUser.firstName(),
                googleUser.lastName(),
                googleUser.email(),
                googleUser.profilePhotoUrl()
        );

        return issueTokenPair(user.id(), PrincipalType.USER);
    }

    @Override
    @Transactional
    public AuthTokenResponseDto adminLogin(AdminLoginRequestDto dto) {
        Admin admin = adminService.findByEmailForLogin(dto.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(dto.password(), admin.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
        if (!admin.isActive()) {
            throw new BusinessException(ErrorCode.ADMIN_INACTIVE);
        }

        adminService.recordSuccessfulLogin(admin.getId());
        return issueTokenPair(admin.getId(), PrincipalType.ADMIN);
    }

    @Override
    @Transactional
    public AuthTokenResponseDto refresh(String rawRefreshToken) {
        String hash = hashToken(rawRefreshToken);
        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_OR_EXPIRED_TOKEN));

        if (!storedToken.isValid()) {
            throw new BusinessException(ErrorCode.INVALID_OR_EXPIRED_TOKEN);
        }

        // Rotation: eski token iptal edilir, yenisi verilir — çalınan bir token'ın
        // sonsuza kadar kullanılabilmesi engellenir.
        storedToken.revoke();

        activityLogService.log(storedToken.getPrincipalId(),
                storedToken.getPrincipalType() == PrincipalType.USER ? LogActorType.USER : LogActorType.ADMIN,
                LogEventType.TOKEN_REFRESHED,
                storedToken.getPrincipalType() == PrincipalType.USER ? TargetType.USER : TargetType.ADMIN,
                storedToken.getPrincipalId(), null);

        return issueTokenPair(storedToken.getPrincipalId(), storedToken.getPrincipalType());
    }

    @Override
    @Transactional
    public void logout(String rawRefreshToken) {
        String hash = hashToken(rawRefreshToken);
        refreshTokenRepository.findByTokenHash(hash).ifPresent(RefreshToken::revoke);
    }

    // ------------------------------------------------------------
    // Private yardımcılar
    // ------------------------------------------------------------

    private AuthTokenResponseDto issueTokenPair(UUID principalId, PrincipalType type) {
        String accessToken = jwtTokenProvider.generateAccessToken(principalId, type);
        String rawRefreshToken = SecureTokenGenerator.generate();

        RefreshToken refreshToken = RefreshToken.builder()
                .principalId(principalId)
                .principalType(type)
                .tokenHash(hashToken(rawRefreshToken))
                .expiresAt(Instant.now().plus(refreshTokenExpirationMs, ChronoUnit.MILLIS))
                .build();
        refreshTokenRepository.save(refreshToken);

        return AuthTokenResponseDto.of(accessToken, rawRefreshToken, jwtTokenProvider.getAccessTokenExpirationSeconds());
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algoritması bulunamadı", e);
        }
    }
}