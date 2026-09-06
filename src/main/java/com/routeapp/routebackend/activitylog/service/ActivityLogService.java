package com.routeapp.routebackend.activitylog.service;

import com.routeapp.routebackend.activitylog.entity.ActivityLog;
import com.routeapp.routebackend.activitylog.entity.LogActorType;
import com.routeapp.routebackend.activitylog.entity.LogEventType;
import com.routeapp.routebackend.common.enums.TargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ActivityLogService {

    void log(UUID actorId, LogActorType actorType, LogEventType eventType,
             TargetType targetType, UUID targetId, String detail);

    Page<ActivityLog> getActorHistory(UUID actorId, LogActorType actorType, Pageable pageable);

    Page<ActivityLog> getTargetHistory(TargetType targetType, UUID targetId, Pageable pageable);

    Page<ActivityLog> getSystemFeed(Pageable pageable);
}