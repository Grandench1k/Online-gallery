package com.online.gallery.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetCompleteRequest {

    @Schema(
            name = "password",
            example = "password123",
            minLength = 8,
            maxLength = 50
    )
    @Size(min = 8, max = 50, message = "password must contain between 8 characters and 50 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=\\S+$).{8,}$",
            message = "the password must contain at least 1 digit and be without spaces"
    )
    @NotNull(message = "password is empty")
    private String password;
}
