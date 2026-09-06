package com.routeapp.routebackend.user.entity.accountlog;

import com.routeapp.routebackend.common.entity.BaseEntity;
import com.routeapp.routebackend.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "user_account_logs",
        indexes = {
                @Index(name = "idx_user_account_logs_user_id", columnList = "user_id"),
                @Index(name = "idx_user_account_logs_created_at", columnList = "created_at")
        }
)
public class UserAccountLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "log_type", nullable = false, length = 40)
    private UserAccountLogType logType;
}