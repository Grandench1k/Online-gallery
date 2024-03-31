package com.online.gallery.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
@Setter
public class NotFoundExceptionResponse {
    ZonedDateTime timestamp = ZonedDateTime.now(ZoneId.of("Z"));
    @Schema(name = "status", example = "404", defaultValue = "404")
    int status = HttpStatus.NOT_FOUND.value();
    @Schema(name = "message", example = "not found", defaultValue = "not found message")
    String message;

    public NotFoundExceptionResponse(String message) {
        this.message = message;
    }
}
