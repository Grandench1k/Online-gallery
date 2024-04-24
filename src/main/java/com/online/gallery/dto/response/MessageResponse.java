package com.online.gallery.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
@Setter
public class MessageResponse {

    @Schema(description = "HTTP status code for successful operation",
            example = "200", defaultValue = "200")
    private final int status;

    @Schema(description = "timestamp of when the error was sent",
            example = "2021-04-12T10:15:30+00:00")
    private final ZonedDateTime timestamp;

    @Schema(description = "success error",
            example = "your request has been processed successfully",
            defaultValue = "operation successful")
    private final String message;

    public MessageResponse(String message) {
        this.status = HttpStatus.OK.value();
        this.timestamp = ZonedDateTime.now(ZoneId.of("UTC"));
        this.message = message;
    }
}
