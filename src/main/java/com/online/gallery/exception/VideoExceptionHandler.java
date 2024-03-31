package com.online.gallery.exception;

import com.online.gallery.controller.video.VideoController;
import com.online.gallery.dto.response.BadRequestResponse;
import com.online.gallery.dto.response.NotFoundResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = VideoController.class)
public class VideoExceptionHandler {
    @ExceptionHandler(VideoNotFoundException.class)
    public ResponseEntity<NotFoundResponse> handleNotFoundVideo(VideoNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new NotFoundResponse(e.getMessage()));
    }

    @ExceptionHandler(VideoDuplicationException.class)
    public ResponseEntity<BadRequestResponse> handleAlreadyDefinedVideo(VideoDuplicationException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new BadRequestResponse(e.getMessage()));
    }

}
