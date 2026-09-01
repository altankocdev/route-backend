package com.routeapp.routebackend.common.exception;

import com.routeapp.routebackend.common.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Uygulama genelinde oluşan exception'ları yakalar
 * ve standart HTTP hata cevaplarına dönüştürür.
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException exception,
            HttpServletRequest request,
            Locale locale
    ) {
        ErrorCode errorCode = exception.getErrorCode();
        String message = messageSource.getMessage(errorCode.getMessageKey(), exception.getArgs(), locale);

        log.warn("BusinessException: {} - {}", errorCode.name(), message);

        ErrorResponse response = ErrorResponse.of(
                errorCode.getHttpStatus().value(),
                errorCode.name(),
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request,
            Locale locale
    ) {
        List<ErrorResponse.FieldError> fieldErrors = new ArrayList<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            fieldErrors.add(new ErrorResponse.FieldError(
                    fieldError.getField(),
                    fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Invalid value"
            ));
        }

        log.warn("Validation failed: {}", fieldErrors);

        String message = messageSource.getMessage(ErrorCode.VALIDATION_FAILED.getMessageKey(), null, locale);

        ErrorResponse response = ErrorResponse.ofValidation(
                ErrorCode.VALIDATION_FAILED.getHttpStatus().value(),
                ErrorCode.VALIDATION_FAILED.name(),
                message,
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.status(ErrorCode.VALIDATION_FAILED.getHttpStatus()).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request,
            Locale locale
    ) {
        log.warn("Method not allowed: {} {} -> {}", exception.getMethod(), request.getRequestURI(), exception.getMessage());
        return buildSimpleError(ErrorCode.METHOD_NOT_ALLOWED, request, locale);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException exception,
            HttpServletRequest request,
            Locale locale
    ) {
        log.warn("Missing request parameter: {} -> {}", request.getRequestURI(), exception.getMessage());
        return buildSimpleError(ErrorCode.MISSING_REQUEST_PARAMETER, request, locale);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request,
            Locale locale
    ) {
        log.warn("Type mismatch for parameter '{}': {} -> {}", exception.getName(), request.getRequestURI(), exception.getMessage());
        return buildSimpleError(ErrorCode.TYPE_MISMATCH, request, locale);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedRequestBody(
            HttpMessageNotReadableException exception,
            HttpServletRequest request,
            Locale locale
    ) {
        log.warn("Malformed request body: {} -> {}", request.getRequestURI(), exception.getMessage());
        return buildSimpleError(ErrorCode.MALFORMED_REQUEST_BODY, request, locale);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException exception,
            HttpServletRequest request,
            Locale locale
    ) {
        log.warn("Data integrity violation: {}", exception.getMessage());
        return buildSimpleError(ErrorCode.BUSINESS_RULE_VIOLATION, request, locale);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request,
            Locale locale
    ) {
        log.error("Beklenmeyen hata: {}", exception.getMessage(), exception);
        return buildSimpleError(ErrorCode.INTERNAL_SERVER_ERROR, request, locale);
    }

    // ---- Ortak yardımcı metod ----
    private ResponseEntity<ErrorResponse> buildSimpleError(ErrorCode errorCode, HttpServletRequest request, Locale locale) {
        String message = messageSource.getMessage(errorCode.getMessageKey(), null, locale);
        ErrorResponse response = ErrorResponse.of(
                errorCode.getHttpStatus().value(),
                errorCode.name(),
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }
}