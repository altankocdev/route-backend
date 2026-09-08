package com.routeapp.routebackend.auth.entity;

public enum PrincipalType {
    USER,
    ADMIN;

    public String toAuthority() {
        return "ROLE_" + this.name();
    }
}