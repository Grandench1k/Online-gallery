package com.online.gallery.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetStartRequest {
    @Email(message = "invalid email address")
    @NotNull(message = "email is empty")
    @Schema(name = "email", example = "email@example.org", defaultValue = "email", minLength = 2, maxLength = 320)
    private String email;
}
