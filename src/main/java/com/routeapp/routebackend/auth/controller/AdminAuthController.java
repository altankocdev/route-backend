package com.routeapp.routebackend.auth.controller;

import com.routeapp.routebackend.auth.dto.request.AdminLoginRequestDto;
import com.routeapp.routebackend.auth.dto.request.RefreshTokenRequestDto;
import com.routeapp.routebackend.auth.dto.response.AuthTokenResponseDto;
import com.routeapp.routebackend.auth.service.AuthService;
import com.routeapp.routebackend.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthTokenResponseDto>> login(@Valid @RequestBody AdminLoginRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success(authService.adminLogin(dto)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthTokenResponseDto>> refresh(@Valid @RequestBody RefreshTokenRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success(authService.refresh(dto.refreshToken())));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody RefreshTokenRequestDto dto) {
        authService.logout(dto.refreshToken());
        return ResponseEntity.ok(ApiResponse.success("Çıkış yapıldı"));
    }
}