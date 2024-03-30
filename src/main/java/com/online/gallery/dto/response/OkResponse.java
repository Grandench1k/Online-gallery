package com.online.gallery.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
public class OkResponse {
    ZonedDateTime timestamp = ZonedDateTime.now(ZoneId.of("Z"));
    @Schema(name = "status", example = "200", defaultValue = "200")
    int status = HttpStatus.OK.value();
    @Schema(name = "message", example = "ok message", defaultValue = "conflict message")
    String message;

    public  OkResponse (String message) {
        this.message = message;
    }
}
