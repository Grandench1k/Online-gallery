package com.online.gallery.exception.video;

import com.online.gallery.controller.video.VideoController;
import com.online.gallery.dto.response.BadRequestExceptionResponse;
import com.online.gallery.dto.response.NotFoundExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = VideoController.class)
public class VideoExceptionHandler {
    @ExceptionHandler(VideoNotFoundException.class)
    public ResponseEntity<NotFoundExceptionResponse> handleNotFoundVideo(VideoNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new NotFoundExceptionResponse(e.getMessage()));
    }

    @ExceptionHandler(VideoDuplicationException.class)
    public ResponseEntity<BadRequestExceptionResponse> handleAlreadyDefinedVideo(VideoDuplicationException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new BadRequestExceptionResponse(e.getMessage()));
    }

}
