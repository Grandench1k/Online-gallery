package com.online.gallery.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
@RequiredArgsConstructor
public class NotFoundResponse {
    ZonedDateTime timestamp = ZonedDateTime.now(ZoneId.of("Z"));
    @Schema(name = "status", example = "404", defaultValue = "404")
    int status = HttpStatus.NOT_FOUND.value();
    @Schema(name = "message", example = "not found", defaultValue = "not found message")
    String message;

    public NotFoundResponse(String message) {
        this.message = message;
    }
}
