package com.routeapp.routebackend.auth.service;

import com.routeapp.routebackend.auth.dto.request.AdminLoginRequestDto;
import com.routeapp.routebackend.auth.dto.request.GoogleLoginRequestDto;
import com.routeapp.routebackend.auth.dto.request.LoginRequestDto;
import com.routeapp.routebackend.auth.dto.request.RegisterRequestDto;
import com.routeapp.routebackend.auth.dto.response.AuthTokenResponseDto;

public interface AuthService {

    AuthTokenResponseDto register(RegisterRequestDto dto);

    AuthTokenResponseDto login(LoginRequestDto dto);

    AuthTokenResponseDto googleLogin(GoogleLoginRequestDto dto);

    AuthTokenResponseDto adminLogin(AdminLoginRequestDto dto);

    AuthTokenResponseDto refresh(String rawRefreshToken);

    void logout(String rawRefreshToken);
}