package com.routeapp.routebackend.user.entity.audit;

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
        name = "admin_audit_logs",
        indexes = {
                @Index(name = "idx_admin_audit_logs_admin_id", columnList = "admin_id"),
                @Index(name = "idx_admin_audit_logs_target", columnList = "target_type, target_id"),
                @Index(name = "idx_admin_audit_logs_created_at", columnList = "created_at")
        }
)
public class AdminAuditLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 30)
    private AdminActionType actionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    private TargetType targetType;

    @Column(name = "target_id", nullable = false)
    private UUID targetId;

    @Column(name = "reason", length = 500)
    private String reason;
}