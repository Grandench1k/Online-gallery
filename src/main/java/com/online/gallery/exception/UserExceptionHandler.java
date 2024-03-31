package com.online.gallery.exception;

import com.online.gallery.controller.UserController;
import com.online.gallery.dto.response.BadRequestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = UserController.class)
public class UserExceptionHandler {
    @ExceptionHandler(PasswordsMatchException.class)
    public ResponseEntity<BadRequestResponse> handlePasswordsMatch(PasswordsMatchException e) {
        BadRequestResponse response = new BadRequestResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<BadRequestResponse> handleInvalidPassword(WrongPasswordException e) {
        BadRequestResponse response = new BadRequestResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
