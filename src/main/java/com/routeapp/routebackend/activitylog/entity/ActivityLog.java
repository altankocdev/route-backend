package com.routeapp.routebackend.activitylog.entity;

import com.routeapp.routebackend.common.entity.BaseEntity;
import com.routeapp.routebackend.common.enums.TargetType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "activity_logs",
        indexes = {
                @Index(name = "idx_activity_logs_actor", columnList = "actor_type, actor_id"),
                @Index(name = "idx_activity_logs_target", columnList = "target_type, target_id"),
                @Index(name = "idx_activity_logs_created_at", columnList = "created_at")
        }
)
public class ActivityLog extends BaseEntity {

    @Column(name = "actor_id")
    private UUID actorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "actor_type", nullable = false, length = 20)
    private LogActorType actorType;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 40)
    private LogEventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    private TargetType targetType;

    @Column(name = "target_id", nullable = false)
    private UUID targetId;

    @Column(name = "detail", length = 500)
    private String detail;
}