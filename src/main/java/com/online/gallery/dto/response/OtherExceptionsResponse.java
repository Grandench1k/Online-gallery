package com.online.gallery.dto.response;

import java.time.ZonedDateTime;


public record OtherExceptionsResponse(ZonedDateTime timestamp, int status, String message) {
}
