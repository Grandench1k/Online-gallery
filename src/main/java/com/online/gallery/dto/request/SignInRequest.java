package com.online.gallery.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInRequest {

    @Schema(name = "email", example = "user@example.com", minLength = 3, maxLength = 320)
    @Email(message = "invalid email address")
    @NotNull(message = "email is empty")
    @Size(min = 3, max = 320, message = "email must contain between 3 characters and 320 characters")
    private String email;

    @Schema(name = "password", example = "Secure1234", minLength = 8, maxLength = 30)
    @NotNull(message = "password is empty")
    private String password;
}
