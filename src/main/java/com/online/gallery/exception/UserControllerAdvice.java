package com.online.gallery.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.online.gallery.controller.UserController;
import com.online.gallery.dto.response.BadRequestResponse;
import com.online.gallery.dto.response.NotFoundResponse;

@RestControllerAdvice(basePackageClasses = UserController.class)
public class UserControllerAdvice {
    @ExceptionHandler(UserImageNotFound.class)
    public ResponseEntity<NotFoundResponse> handleNotFoundUserImage(UserImageNotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new NotFoundResponse(e.getMessage()));
    }
    @ExceptionHandler(PasswordsMatch.class)
    public ResponseEntity<BadRequestResponse> handlePasswordsMatch(PasswordsMatch e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadRequestResponse(e.getMessage()));
    }
    @ExceptionHandler(InvalidPassword.class)
    public ResponseEntity<BadRequestResponse> handleInvalidPassword(InvalidPassword e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadRequestResponse(e.getMessage()));
    }
}
