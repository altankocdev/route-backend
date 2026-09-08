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
    USER_FIRST_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "user.firstNameRequired"),
    USER_LAST_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "user.lastNameRequired"),
    USER_EMAIL_REQUIRED(HttpStatus.BAD_REQUEST, "user.emailRequired"),
    USER_PASSWORD_REQUIRED(HttpStatus.BAD_REQUEST, "user.passwordRequired"),
    USER_USERNAME_REQUIRED(HttpStatus.BAD_REQUEST, "user.usernameRequired"),
    USER_USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "user.usernameAlreadyExists"),
    USER_USERNAME_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "user.usernameInvalidFormat"),

    EMAIL_ALREADY_VERIFIED(HttpStatus.CONFLICT, "user.emailAlreadyVerified"),
    EMAIL_NOT_VERIFIED(HttpStatus.FORBIDDEN, "user.emailNotVerified"),
    INVALID_OR_EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "auth.invalidOrExpiredToken"),

    // Location
    LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "location.notFound"),
    LOCATION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "location.accessDenied"),

    // Activity / Route
    ACTIVITY_NOT_FOUND(HttpStatus.NOT_FOUND, "activity.notFound"),
    ACTIVITY_ACCESS_DENIED(HttpStatus.FORBIDDEN, "activity.accessDenied"),
    ACTIVITY_STOP_REQUIRED(HttpStatus.BAD_REQUEST, "activity.stopRequired"),

    // Admin
    ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "admin.notFound"),
    ADMIN_INACTIVE(HttpStatus.FORBIDDEN, "admin.inactive"),
    GOOGLE_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "auth.googleTokenInvalid"),
    GOOGLE_ACCOUNT_ACTION_NOT_ALLOWED(HttpStatus.FORBIDDEN, "auth.googleAccountActionNotAllowed"),

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