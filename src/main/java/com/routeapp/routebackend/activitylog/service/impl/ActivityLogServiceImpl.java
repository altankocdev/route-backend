package com.routeapp.routebackend.activitylog.service.impl;

import com.routeapp.routebackend.activitylog.entity.ActivityLog;
import com.routeapp.routebackend.activitylog.entity.LogActorType;
import com.routeapp.routebackend.activitylog.entity.LogEventType;
import com.routeapp.routebackend.activitylog.repository.ActivityLogRepository;
import com.routeapp.routebackend.activitylog.service.ActivityLogService;
import com.routeapp.routebackend.common.enums.TargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    @Override
    @Transactional
    public void log(UUID actorId, LogActorType actorType, LogEventType eventType,
                    TargetType targetType, UUID targetId, String detail) {
        activityLogRepository.save(
                ActivityLog.builder()
                        .actorId(actorId)
                        .actorType(actorType)
                        .eventType(eventType)
                        .targetType(targetType)
                        .targetId(targetId)
                        .detail(detail)
                        .build()
        );
    }

    @Override
    public Page<ActivityLog> getActorHistory(UUID actorId, LogActorType actorType, Pageable pageable) {
        return activityLogRepository.findByActorIdAndActorTypeOrderByCreatedAtDesc(actorId, actorType, pageable);
    }

    @Override
    public Page<ActivityLog> getTargetHistory(TargetType targetType, UUID targetId, Pageable pageable) {
        return activityLogRepository.findByTargetTypeAndTargetIdOrderByCreatedAtDesc(targetType, targetId, pageable);
    }

    @Override
    public Page<ActivityLog> getSystemFeed(Pageable pageable) {
        return activityLogRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
}