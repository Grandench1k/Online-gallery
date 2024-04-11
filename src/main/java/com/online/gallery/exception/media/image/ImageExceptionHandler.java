package com.online.gallery.exception.media.image;

import com.online.gallery.controller.media.image.ImageController;
import com.online.gallery.dto.response.BadRequestExceptionResponse;
import com.online.gallery.dto.response.NotFoundExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = ImageController.class)
public class ImageExceptionHandler {
    @ExceptionHandler(ImageNotFoundException.class)
    public ResponseEntity<NotFoundExceptionResponse> handleNotFoundImage(ImageNotFoundException e) {
        NotFoundExceptionResponse notFoundExceptionResponse = new NotFoundExceptionResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundExceptionResponse);
    }

    @ExceptionHandler(ImageDuplicationException.class)
    public ResponseEntity<BadRequestExceptionResponse> handleAlreadyDefinedImage(ImageDuplicationException e) {
        BadRequestExceptionResponse badRequestExceptionResponse = new BadRequestExceptionResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(badRequestExceptionResponse);
    }
}
