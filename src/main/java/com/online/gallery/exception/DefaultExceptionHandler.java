package com.online.gallery.exception;

import com.online.gallery.dto.response.DefaultExceptionResponse;
import jakarta.mail.MessagingException;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import com.online.gallery.dto.response.BadRequestResponse;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class DefaultExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BadRequestResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        BadRequestResponse response = new BadRequestResponse(String.join(", ", errors));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<BadRequestResponse> handleNullPointerException() {
        BadRequestResponse response = new BadRequestResponse("One of the variables is null");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(InvalidContentTypeException.class)
    public ResponseEntity<DefaultExceptionResponse> handleInvalidContentTypeException(InvalidContentTypeException e) {
        DefaultExceptionResponse response = new DefaultExceptionResponse(
                ZonedDateTime.now(ZoneId.of("Z")),
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                e.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BadRequestResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        BadRequestResponse response = new BadRequestResponse(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<DefaultExceptionResponse> handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException e) {
        String answer = e.getMessage() + ". Use by default application/json or multipart/form-data if you send media";

        DefaultExceptionResponse response = new DefaultExceptionResponse(
                ZonedDateTime.now(ZoneId.of("Z")),
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                answer);

        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(response);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<BadRequestResponse> handleMissingServletRequestPartException(
            MissingServletRequestPartException e) {
        BadRequestResponse response = new BadRequestResponse(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<DefaultExceptionResponse> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        DefaultExceptionResponse response = new DefaultExceptionResponse(
                ZonedDateTime.now(ZoneId.of("Z")),
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                e.getMessage());
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(response);
    }

    @ExceptionHandler(FileSizeLimitExceededException.class)
    public ResponseEntity<BadRequestResponse> handleFileSizeLimitExceededException() {
        BadRequestResponse response = new BadRequestResponse("File limit exceeded 20 megabytes");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<BadRequestResponse> handleMessagingException() {
        BadRequestResponse response = new BadRequestResponse("Messaging exception");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<BadRequestResponse> handleNotFoundConfirmationToken(TokenNotFoundException e) {
        BadRequestResponse response = new BadRequestResponse(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(InvalidFilenameException.class)
    public ResponseEntity<BadRequestResponse> handleInvalidFilename(InvalidFilenameException e) {
        BadRequestResponse response = new BadRequestResponse(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }
}
