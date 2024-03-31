package com.online.gallery.exception.user;

import com.online.gallery.controller.user.UserController;
import com.online.gallery.dto.response.BadRequestExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = UserController.class)
public class UserExceptionHandler {
    @ExceptionHandler(PasswordsMatchException.class)
    public ResponseEntity<BadRequestExceptionResponse> handlePasswordsMatch(PasswordsMatchException e) {
        BadRequestExceptionResponse response = new BadRequestExceptionResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<BadRequestExceptionResponse> handleInvalidPassword(WrongPasswordException e) {
        BadRequestExceptionResponse response = new BadRequestExceptionResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
