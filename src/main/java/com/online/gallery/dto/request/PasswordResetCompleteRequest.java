package com.online.gallery.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class PasswordResetCompleteRequest {
    @Schema(name = "password", example = "password123", minLength = 8, maxLength = 50)
    @Length(min = 8, max = 50, message = "password must contain more than 8 characters and less than 50 characters")
    @NotNull(message = "Password is empty")
    @Pattern(regexp = "^(?=.*[0-9])(?=\\S+$).{8,}$", message = "the password must contain at least 1 digit and be without spaces")
    private String password;
}
