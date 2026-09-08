package com.routeapp.routebackend.user.controller;

import com.routeapp.routebackend.auth.security.AuthenticatedPrincipal;
import com.routeapp.routebackend.common.response.ApiResponse;
import com.routeapp.routebackend.user.dto.response.UserResponseDto;
import com.routeapp.routebackend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> getMyProfile(
            @AuthenticationPrincipal AuthenticatedPrincipal principal
    ) {
        return ResponseEntity.ok(ApiResponse.success(userService.getOwnProfile(principal.id())));
    }
}