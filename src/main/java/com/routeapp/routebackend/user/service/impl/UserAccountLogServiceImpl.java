package com.routeapp.routebackend.user.service.impl;

import com.routeapp.routebackend.user.entity.User;
import com.routeapp.routebackend.user.entity.accountlog.UserAccountLog;
import com.routeapp.routebackend.user.entity.accountlog.UserAccountLogType;
import com.routeapp.routebackend.user.repository.UserAccountLogRepository;
import com.routeapp.routebackend.user.service.UserAccountLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserAccountLogServiceImpl implements UserAccountLogService {

    private final UserAccountLogRepository userAccountLogRepository;

    @Override
    @Transactional
    public void record(User user, UserAccountLogType logType) {
        userAccountLogRepository.save(
                UserAccountLog.builder()
                        .user(user)
                        .logType(logType)
                        .build()
        );
    }

    @Override
    public Page<UserAccountLog> getHistory(UUID userId, Pageable pageable) {
        return userAccountLogRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
}