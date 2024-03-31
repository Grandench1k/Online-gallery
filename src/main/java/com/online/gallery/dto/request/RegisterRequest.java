package com.online.gallery.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class RegisterRequest {
    @Id
    @Schema(hidden = true)
    private String id;
    @Schema(name = "username", example = "John Doe", defaultValue = "username", minLength = 2, maxLength = 20)
    @Length(min = 2, max = 20, message = "username must contain more than 2 characters and less than 20 characters")
    @NotNull(message = "username is empty")
    private String username;
    @Email(message = "invalid email address.")
    @NotNull(message = "email is empty")
    @Schema(name = "email", example = "email@example.org", defaultValue = "email", minLength = 2, maxLength = 320)
    @Length(min = 3, max = 320, message = "email must contain more than 3 characters and less than 320 characters")
    private String email;
    @Schema(name = "password", example = "password123", minLength = 8, maxLength = 30)
    @Length(min = 8, max = 30, message = "password must contain more than 8 characters and less than 30 characters")
    @NotNull(message = "Password is empty")
    @Pattern(regexp = "^(?=.*[0-9])(?=\\S+$).{8,}$", message = "the password must contain at least 1 digit and be without spaces")
    private String password;
}
