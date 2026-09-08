package com.routeapp.routebackend.admin.service.impl;

import com.routeapp.routebackend.activitylog.entity.LogActorType;
import com.routeapp.routebackend.activitylog.entity.LogEventType;
import com.routeapp.routebackend.activitylog.service.ActivityLogService;
import com.routeapp.routebackend.admin.dto.request.ChangeAdminPasswordRequestDto;
import com.routeapp.routebackend.admin.dto.response.AdminResponseDto;
import com.routeapp.routebackend.admin.entity.Admin;
import com.routeapp.routebackend.admin.mapper.AdminMapper;
import com.routeapp.routebackend.admin.repository.AdminRepository;
import com.routeapp.routebackend.admin.service.AdminService;
import com.routeapp.routebackend.common.enums.TargetType;
import com.routeapp.routebackend.common.exception.BusinessException;
import com.routeapp.routebackend.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;
    private final ActivityLogService activityLogService;

    @Override
    public Admin getByIdOrThrow(UUID adminId) {
        return adminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));
    }

    @Override
    public Admin getByEmailOrThrow(String email) {
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));
    }

    @Override
    public AdminResponseDto getOwnProfile(UUID adminId) {
        return adminMapper.toResponseDto(getByIdOrThrow(adminId));
    }

    @Override
    @Transactional
    public void changePassword(UUID adminId, ChangeAdminPasswordRequestDto dto) {
        Admin admin = getByIdOrThrow(adminId);
        if (!passwordEncoder.matches(dto.currentPassword(), admin.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
        admin.changePasswordHash(passwordEncoder.encode(dto.newPassword()));
        activityLogService.log(admin.getId(), LogActorType.ADMIN, LogEventType.ADMIN_PASSWORD_CHANGED,
                TargetType.ADMIN, admin.getId(), null);
    }

    @Override
    @Transactional
    public void deactivate(UUID adminId) {
        Admin admin = getByIdOrThrow(adminId);
        admin.deactivate();
        activityLogService.log(adminId, LogActorType.SYSTEM, LogEventType.ADMIN_DEACTIVATED,
                TargetType.ADMIN, adminId, null);
    }

    @Override
    @Transactional
    public void reactivate(UUID adminId) {
        Admin admin = getByIdOrThrow(adminId);
        admin.activate();
        activityLogService.log(adminId, LogActorType.SYSTEM, LogEventType.ADMIN_REACTIVATED,
                TargetType.ADMIN, adminId, null);
    }

    @Override
    @Transactional
    public void recordSuccessfulLogin(UUID adminId) {
        activityLogService.log(adminId, LogActorType.ADMIN, LogEventType.ADMIN_LOGGED_IN,
                TargetType.ADMIN, adminId, null);
    }
}