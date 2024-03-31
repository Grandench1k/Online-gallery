package com.online.gallery.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
@Setter
public class BadRequestResponse {
    ZonedDateTime timestamp = ZonedDateTime.now(ZoneId.of("Z"));
    @Schema(name = "status", example = "400", defaultValue = "400")
    int status = HttpStatus.BAD_REQUEST.value();
    @Schema(name = "message", example = "bad request", defaultValue = "bad request message")
    String message;

    public BadRequestResponse(String message) {
        this.message = message;
    }
}
