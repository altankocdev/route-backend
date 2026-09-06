package com.routeapp.routebackend.user.entity.audit;

public enum AdminActionType {
    USER_BANNED,
    USER_UNBANNED,
    USER_DELETED,
    LOCATION_REMOVED,
    ACTIVITY_REMOVED,
    REPORT_RESOLVED,
    REPORT_DISMISSED,
    ROLE_CHANGED
}