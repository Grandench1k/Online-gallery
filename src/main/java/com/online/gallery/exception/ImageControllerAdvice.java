package com.online.gallery.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.online.gallery.controller.ImageController;
import com.online.gallery.dto.response.BadRequestResponse;
import com.online.gallery.dto.response.NotFoundResponse;

@RestControllerAdvice(basePackageClasses = ImageController.class)
public class ImageControllerAdvice {
    @ExceptionHandler(ImageNotFound.class)
    public ResponseEntity<NotFoundResponse> handleNotFoundImage(ImageNotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new NotFoundResponse(e.getMessage()));
    }

    @ExceptionHandler(ImageAlreadyDefined.class)
    public ResponseEntity<BadRequestResponse> handleAlreadyDefinedImage(ImageAlreadyDefined e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadRequestResponse(e.getMessage()));
    }


}
