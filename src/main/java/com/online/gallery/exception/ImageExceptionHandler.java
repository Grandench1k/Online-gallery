package com.online.gallery.exception;

import com.online.gallery.controller.ImageController;
import com.online.gallery.dto.response.BadRequestResponse;
import com.online.gallery.dto.response.NotFoundResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = ImageController.class)
public class ImageExceptionHandler {
    @ExceptionHandler(ImageNotFoundException.class)
    public ResponseEntity<NotFoundResponse> handleNotFoundImage(ImageNotFoundException e) {
        NotFoundResponse notFoundResponse = new NotFoundResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundResponse);
    }

    @ExceptionHandler(ImageDuplicationException.class)
    public ResponseEntity<BadRequestResponse> handleAlreadyDefinedImage(ImageDuplicationException e) {
        BadRequestResponse badRequestResponse = new BadRequestResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(badRequestResponse);
    }
}
