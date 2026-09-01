package com.routeapp.routebackend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Uygulamada oluşabilecek kontrollü hata türlerini,
 * HTTP durumlarını ve mesaj anahtarlarını merkezi olarak tutar.
 */
@Getter
public enum ErrorCode {

    // Common
    BUSINESS_RULE_VIOLATION(HttpStatus.BAD_REQUEST, "business.ruleViolation"),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "common.validationFailed"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "common.methodNotAllowed"),
    MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "common.missingParameter"),
    TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "common.typeMismatch"),
    MALFORMED_REQUEST_BODY(HttpStatus.BAD_REQUEST, "common.malformedRequestBody"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "common.internalServerError"),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "user.notFound"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "user.emailAlreadyExists"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "auth.invalidCredentials"),
    USER_INACTIVE(HttpStatus.FORBIDDEN, "user.inactive"),

    // Location
    LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "location.notFound"),
    LOCATION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "location.accessDenied"),

    // Activity / Route
    ACTIVITY_NOT_FOUND(HttpStatus.NOT_FOUND, "activity.notFound"),
    ACTIVITY_ACCESS_DENIED(HttpStatus.FORBIDDEN, "activity.accessDenied"),
    ACTIVITY_STOP_REQUIRED(HttpStatus.BAD_REQUEST, "activity.stopRequired"),

    // Photo
    PHOTO_NOT_FOUND(HttpStatus.NOT_FOUND, "photo.notFound"),
    PHOTO_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "photo.uploadFailed");

    private final HttpStatus httpStatus;
    private final String messageKey;

    ErrorCode(HttpStatus httpStatus, String messageKey) {
        this.httpStatus = httpStatus;
        this.messageKey = messageKey;
    }
}