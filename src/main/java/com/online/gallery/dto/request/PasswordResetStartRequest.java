package com.online.gallery.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetStartRequest {
    @Schema(
            name = "email",
            example = "email@example.org",
            defaultValue = "email",
            minLength = 2,
            maxLength = 320
    )
    @Email(message = "invalid email address")
    @Size(min = 2, max = 320, message = "email must contain between 2 characters and 320 characters")
    @NotNull(message = "email is empty")
    private String email;
}
