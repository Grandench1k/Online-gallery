package com.online.gallery.exception.auth;

import com.online.gallery.controller.auth.AuthController;
import com.online.gallery.dto.response.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@RestControllerAdvice(basePackageClasses = AuthController.class)
public class AuthenticationExceptionHandler {

    private static final ZoneId UTC = ZoneId.of("UTC");

    private ResponseEntity<ExceptionResponse> buildResponse(String message, HttpStatus status) {
        ExceptionResponse response = new ExceptionResponse(
                ZonedDateTime.now(UTC),
                status.value(),
                message);
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleUserNotFoundException(RuntimeException e) {
        return buildResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({TokenExpirationException.class,
            AuthException.class})
    public ResponseEntity<ExceptionResponse> handleBadRequestExceptions(RuntimeException e) {
        return buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenDuplicationException.class)
    public ResponseEntity<ExceptionResponse> handleConflictExceptions(RuntimeException e) {
        return buildResponse(e.getMessage(), HttpStatus.CONFLICT);
    }
}
