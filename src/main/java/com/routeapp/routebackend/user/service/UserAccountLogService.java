package com.routeapp.routebackend.user.service;

import com.routeapp.routebackend.user.entity.User;
import com.routeapp.routebackend.user.entity.accountlog.UserAccountLog;
import com.routeapp.routebackend.user.entity.accountlog.UserAccountLogType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserAccountLogService {

    void record(User user, UserAccountLogType logType);

    Page<UserAccountLog> getHistory(UUID userId, Pageable pageable);
}