package com.routeapp.routebackend.user.service;

import com.routeapp.routebackend.user.dto.request.account.ChangeEmailRequestDto;
import com.routeapp.routebackend.user.dto.request.account.ChangePasswordRequestDto;
import com.routeapp.routebackend.user.dto.request.account.ChangeUsernameRequestDto;
import com.routeapp.routebackend.user.dto.request.account.UpdateProfileRequestDto;
import com.routeapp.routebackend.user.dto.request.registration.RegisterRequestDto;
import com.routeapp.routebackend.user.dto.request.verification.ForgotPasswordRequestDto;
import com.routeapp.routebackend.user.dto.request.verification.ResetPasswordRequestDto;
import com.routeapp.routebackend.user.dto.response.UserResponseDto;
import com.routeapp.routebackend.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    // Sorgulama
    User getByIdOrThrow(UUID userId);

    UserResponseDto getOwnProfile(UUID userId);

    Page<User> searchUsers(String query, Pageable pageable);

    void requireVerifiedEmail(User user);

    Page<User> getActiveUsers(Pageable pageable);

    // Kayıt
    UserResponseDto register(RegisterRequestDto dto);

    UserResponseDto findOrCreateGoogleUser(String googleId, String firstName, String lastName,
                                           String email, String profilePhotoUrl);

    // E-posta doğrulama
    void verifyEmail(String token);

    void resendVerificationEmail(UUID userId);

    // Hesap yönetimi
    UserResponseDto updateProfile(UUID userId, UpdateProfileRequestDto dto);

    void requestEmailChange(UUID userId, ChangeEmailRequestDto dto);

    void confirmEmailChange(String token);

    void changeUsername(UUID userId, ChangeUsernameRequestDto dto);

    void changePassword(UUID userId, ChangePasswordRequestDto dto);

    // Şifremi unuttum
    void forgotPassword(ForgotPasswordRequestDto dto);

    void resetPassword(ResetPasswordRequestDto dto);

    // Hesap dondurma, silme talebi ve iptali
    void deactivateAccount(UUID userId);

    void reactivateAccount(UUID userId);

    void requestAccountDeletion(UUID userId);

    void cancelAccountDeletion(UUID userId);

    // Login sonrası — Security modülü çağıracak
    void recordSuccessfulLogin(UUID userId);
}