package com.online.gallery.exception;

import jakarta.mail.MessagingException;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import com.online.gallery.dto.response.BadRequestResponse;
import com.online.gallery.dto.response.OtherExceptionsResponse;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class DefaultControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BadRequestResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errors = new ArrayList<>();

        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.add(error.getDefaultMessage());
        }

        for (ObjectError error : e.getBindingResult().getGlobalErrors()) {
            errors.add(error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadRequestResponse(errors.toString()));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<BadRequestResponse> handleNullPointerException(NullPointerException e) {
        String answer = "one of variables is null";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadRequestResponse(answer));
    }

    @ExceptionHandler(InvalidContentTypeException.class)
    public ResponseEntity<OtherExceptionsResponse> handleInvalidContentTypeException(InvalidContentTypeException e) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(new OtherExceptionsResponse(ZonedDateTime.now(ZoneId.of("Z")), HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), e.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BadRequestResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadRequestResponse(e.getMessage()));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<OtherExceptionsResponse> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        String answer = e.getMessage() + ". For save you need to use only application/json and multipart/form-data mediaTypes";
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(new OtherExceptionsResponse(ZonedDateTime.now(ZoneId.of("Z")), HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), answer));
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<BadRequestResponse> handleMissingServletRequestPartException(MissingServletRequestPartException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadRequestResponse(e.getMessage()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<OtherExceptionsResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new OtherExceptionsResponse(ZonedDateTime.now(ZoneId.of("Z")), HttpStatus.METHOD_NOT_ALLOWED.value(), e.getMessage()));
    }

    @ExceptionHandler(FileSizeLimitExceededException.class)
    public ResponseEntity<BadRequestResponse> handleFileSizeLimitExceededException(FileSizeLimitExceededException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadRequestResponse("file limit exceeded 20 megabytes."));
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<BadRequestResponse> handleMessagingException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadRequestResponse("messaging exception."));
    }

    @ExceptionHandler(ConfirmationTokenNotFound.class)
    public ResponseEntity<BadRequestResponse> handleNotFoundConfirmationToken(ConfirmationTokenNotFound e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadRequestResponse(e.getMessage()));
    }
}
