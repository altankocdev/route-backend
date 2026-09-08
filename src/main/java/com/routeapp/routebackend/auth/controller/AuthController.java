package com.routeapp.routebackend.auth.controller;

import com.routeapp.routebackend.auth.dto.request.GoogleLoginRequestDto;
import com.routeapp.routebackend.auth.dto.request.LoginRequestDto;
import com.routeapp.routebackend.auth.dto.request.RefreshTokenRequestDto;
import com.routeapp.routebackend.auth.dto.request.RegisterRequestDto;
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
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthTokenResponseDto>> register(@Valid @RequestBody RegisterRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success(authService.register(dto)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthTokenResponseDto>> login(@Valid @RequestBody LoginRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success(authService.login(dto)));
    }

    @PostMapping("/google")
    public ResponseEntity<ApiResponse<AuthTokenResponseDto>> googleLogin(@Valid @RequestBody GoogleLoginRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success(authService.googleLogin(dto)));
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