package com.online.gallery.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.online.gallery.controller.VideoController;
import com.online.gallery.dto.response.BadRequestResponse;
import com.online.gallery.dto.response.NotFoundResponse;

@RestControllerAdvice(basePackageClasses = VideoController.class)
public class VideoControllerAdvice {
    @ExceptionHandler(VideoNotFound.class)
    public ResponseEntity<NotFoundResponse> handleNotFoundVideo(VideoNotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new NotFoundResponse(e.getMessage()));
    }

    @ExceptionHandler(VideoAlreadyDefined.class)
    public ResponseEntity<BadRequestResponse> handleAlreadyDefinedVideo(VideoAlreadyDefined e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadRequestResponse(e.getMessage()));
    }

}
