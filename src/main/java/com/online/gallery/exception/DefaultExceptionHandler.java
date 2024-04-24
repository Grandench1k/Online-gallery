package com.online.gallery.exception;

import com.online.gallery.dto.response.ExceptionResponse;
import com.online.gallery.exception.media.image.ImageDuplicationException;
import com.online.gallery.exception.media.image.ImageNotFoundException;
import com.online.gallery.exception.media.video.VideoDuplicationException;
import com.online.gallery.exception.media.video.VideoNotFoundException;
import com.online.gallery.exception.user.*;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.mail.MessagingException;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class DefaultExceptionHandler {

    private static final ZoneId UTC = ZoneId.of("UTC");

    private ResponseEntity<ExceptionResponse> buildResponse(String message, HttpStatus status) {
        ExceptionResponse response = new ExceptionResponse(
                ZonedDateTime.now(UTC),
                status.value(),
                message);
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return buildResponse(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleBadCredentialsException(
            BadCredentialsException e) {
        return buildResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({UserNotFoundException.class,
            ImageNotFoundException.class,
            VideoNotFoundException.class})
    public ResponseEntity<ExceptionResponse> handleNotFoundExceptions(
            RuntimeException e) {
        return buildResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ExceptionResponse> handleMalformedJwtException(
            MalformedJwtException e) {
        return buildResponse("Invalid JWT token: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({HttpMediaTypeNotSupportedException.class,
            InvalidContentTypeException.class})
    public ResponseEntity<ExceptionResponse> handleUnsupportedMediaTypeExceptions(
            RuntimeException e) {
        return buildResponse(e.getMessage(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e) {
        return buildResponse("Required request body is missing", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ExceptionResponse> handleHttpRequestMethodNotAllowedException(
            HttpRequestMethodNotSupportedException e) {
        return buildResponse(e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler({ImageDuplicationException.class,
            VideoDuplicationException.class,
            UserDuplicationException.class})
    public ResponseEntity<ExceptionResponse> handleConflictExceptions(
            RuntimeException e) {
        return buildResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({PasswordsMatchException.class,
            WrongPasswordException.class,
            UserNotEnabledException.class,
            PasswordsMatchException.class})
    public ResponseEntity<ExceptionResponse> handleHttpRequestMethodNotAllowedExceptions(
            RuntimeException e) {
        return buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NoSuchBucketException.class,
            MessagingException.class,
            NullPointerException.class})
    public ResponseEntity<ExceptionResponse> handleInternalServerErrorExceptions(
            RuntimeException e) {
        return buildResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
