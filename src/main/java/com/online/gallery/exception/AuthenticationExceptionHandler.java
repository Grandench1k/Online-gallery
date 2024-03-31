package com.online.gallery.exception;

import com.online.gallery.controller.auth.AuthenticationController;
import com.online.gallery.dto.response.BadRequestResponse;
import com.online.gallery.dto.response.NotFoundResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = AuthenticationController.class)
public class AuthenticationExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<NotFoundResponse> handleNotFound(UserNotFoundException e) {
        NotFoundResponse response = new NotFoundResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserDuplicationException.class)
    public ResponseEntity<BadRequestResponse> handleAlreadyDefined(UserDuplicationException e) {
        BadRequestResponse response = new BadRequestResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(UserNotEnabledException.class)
    public ResponseEntity<BadRequestResponse> handleUserNotEnabled(UserNotEnabledException e) {
        BadRequestResponse response = new BadRequestResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(TokenExpirationException.class)
    public ResponseEntity<BadRequestResponse> handleConfirmationTokenIsExpired(TokenExpirationException e) {
        BadRequestResponse response = new BadRequestResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(TokenDuplicationException.class)
    public ResponseEntity<BadRequestResponse> handlePasswordResetTokenAlreadyDefined(
            TokenDuplicationException e) {
        BadRequestResponse response = new BadRequestResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
