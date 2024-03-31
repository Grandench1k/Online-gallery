package com.online.gallery.dto.response;

import java.time.ZonedDateTime;


public record DefaultExceptionResponse(ZonedDateTime timestamp, int status, String message) {
}
