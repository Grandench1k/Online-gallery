package com.online.gallery.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
@Setter
public class DataResponse<T> {

    @Schema(description = "HTTP status code of the response", example = "200", defaultValue = "200")
    private final int status;

    @Schema(
            description = "timestamp of when the response was generated",
            example = "2021-04-12T10:15:30+00:00"
    )
    private final ZonedDateTime timestamp;

    @Schema(
            description = "payload of the response containing the requested data or result of the operation",
            example = "{\"name\":\"example\",\"value\":\"123\"}",
            defaultValue = "payload can vary based on the operation and requested data"
    )
    private final T data;

    public DataResponse(T data) {
        this.status = HttpStatus.OK.value();
        this.timestamp = ZonedDateTime.now(ZoneId.of("UTC"));
        this.data = data;
    }
}
