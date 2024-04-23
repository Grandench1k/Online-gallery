package com.online.gallery.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class SignUpRequest {

    @Schema(hidden = true)
    @Id
    private String id;

    @Schema(name = "username", example = "john_doe", minLength = 2, maxLength = 20)
    @Size(min = 2, max = 20, message = "username must contain between 2 characters and 20 characters")
    @NotNull(message = "username is empty")
    private String username;

    @Schema(name = "email", example = "user@example.com", minLength = 3, maxLength = 320)
    @Email(message = "invalid email address")
    @Size(min = 3, max = 320, message = "email must contain between 3 characters and 320 characters")
    @NotNull(message = "email is empty")
    private String email;

    @Schema(name = "password", example = "Secure1234", minLength = 8, maxLength = 30)
    @Size(min = 8, max = 30, message = "password must contain between 8 characters and 30 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=\\S+$).{8,}$", message = "the password must contain at least 1 digit " +
            "and be without spaces")
    @NotNull(message = "password is empty")
    private String password;
}
