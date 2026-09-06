package com.routeapp.routebackend.user.entity.activitylog;

import com.routeapp.routebackend.common.entity.BaseEntity;
import com.routeapp.routebackend.common.enums.TargetType;
import com.routeapp.routebackend.user.entity.User;
import jakarta.persistence.*;
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
        name = "user_activity_logs",
        indexes = {
                @Index(name = "idx_user_activity_logs_user_id", columnList = "user_id"),
                @Index(name = "idx_user_activity_logs_created_at", columnList = "created_at")
        }
)
public class UserActivityLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false, length = 30)
    private UserActivityType activityType;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    private TargetType targetType;

    @Column(name = "target_id", nullable = false)
    private UUID targetId;
}