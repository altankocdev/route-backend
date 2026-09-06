package com.routeapp.routebackend.activitylog.repository;

import com.routeapp.routebackend.activitylog.entity.ActivityLog;
import com.routeapp.routebackend.activitylog.entity.LogActorType;
import com.routeapp.routebackend.common.enums.TargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {

    Page<ActivityLog> findByActorIdAndActorTypeOrderByCreatedAtDesc(
            UUID actorId, LogActorType actorType, Pageable pageable);

    Page<ActivityLog> findByTargetTypeAndTargetIdOrderByCreatedAtDesc(
            TargetType targetType, UUID targetId, Pageable pageable);

    Page<ActivityLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
}