package com.routeapp.routebackend.admin.service;

import com.routeapp.routebackend.admin.dto.request.ChangeAdminPasswordRequestDto;
import com.routeapp.routebackend.admin.dto.response.AdminResponseDto;
import com.routeapp.routebackend.admin.entity.Admin;

import java.util.Optional;
import java.util.UUID;

public interface AdminService {

    Admin getByIdOrThrow(UUID adminId);

    Admin getByEmailOrThrow(String email);

    AdminResponseDto getOwnProfile(UUID adminId);

    Optional<Admin> findByEmailForLogin(String email);

    void changePassword(UUID adminId, ChangeAdminPasswordRequestDto dto);

    void deactivate(UUID adminId);

    void reactivate(UUID adminId);

    void recordSuccessfulLogin(UUID adminId);
}