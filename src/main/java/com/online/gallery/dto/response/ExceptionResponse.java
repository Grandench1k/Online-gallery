package com.online.gallery.dto.response;

import java.time.ZonedDateTime;


public record ExceptionResponse(ZonedDateTime timestamp, int status, String error) {
}
