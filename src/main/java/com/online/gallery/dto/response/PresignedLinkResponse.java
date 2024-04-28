package com.online.gallery.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
@Setter
public class PresignedLinkResponse {

    @Schema(description = "HTTP status code for successful operation",
            example = "200")
    private final int status;

    @Schema(description = "timestamp ofwhen the link was generated",
            example = "2021-04-12T10:15:30+00:00")
    private final ZonedDateTime timestamp;

    @Schema(description = "duration until the presigned link expires, expressed in seconds from now",
            example = "2021-04-12T10:15:40+00:00")
    private final ZonedDateTime duration;

    @Schema(description = "the presigned URL link",
            example = "https://example.com/download?signature=abcdefg&expiry=1618237200")
    private final String link;

    public PresignedLinkResponse(long duration, String link) {
        this.status = HttpStatus.OK.value();
        this.timestamp = ZonedDateTime.now(ZoneId.of("UTC"));
        this.duration = ZonedDateTime.now(ZoneId.of("UTC")).plusSeconds(duration);
        this.link = link;
    }
}
