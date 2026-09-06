package com.routeapp.routebackend.admin.entity;

import com.routeapp.routebackend.common.entity.BaseEntity;
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
        name = "admins",
        uniqueConstraints = @UniqueConstraint(name = "uk_admins_email", columnNames = "email")
)
public class Admin extends BaseEntity {

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    public void changePasswordHash(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }
}