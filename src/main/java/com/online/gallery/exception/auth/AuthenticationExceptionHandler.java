package com.online.gallery.exception.auth;

import com.online.gallery.controller.auth.AuthenticationController;
import com.online.gallery.dto.response.BadRequestExceptionResponse;
import com.online.gallery.dto.response.NotFoundExceptionResponse;
import com.online.gallery.exception.user.UserDuplicationException;
import com.online.gallery.exception.user.UserNotEnabledException;
import com.online.gallery.exception.user.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = AuthenticationController.class)
public class AuthenticationExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<NotFoundExceptionResponse> handleNotFound(UserNotFoundException e) {
        NotFoundExceptionResponse response = new NotFoundExceptionResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserDuplicationException.class)
    public ResponseEntity<BadRequestExceptionResponse> handleAlreadyDefined(UserDuplicationException e) {
        BadRequestExceptionResponse response = new BadRequestExceptionResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(UserNotEnabledException.class)
    public ResponseEntity<BadRequestExceptionResponse> handleUserNotEnabled(UserNotEnabledException e) {
        BadRequestExceptionResponse response = new BadRequestExceptionResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(TokenExpirationException.class)
    public ResponseEntity<BadRequestExceptionResponse> handleConfirmationTokenIsExpired(TokenExpirationException e) {
        BadRequestExceptionResponse response = new BadRequestExceptionResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(TokenDuplicationException.class)
    public ResponseEntity<BadRequestExceptionResponse> handlePasswordResetTokenAlreadyDefined(
            TokenDuplicationException e) {
        BadRequestExceptionResponse response = new BadRequestExceptionResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
