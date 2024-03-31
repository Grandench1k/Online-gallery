package com.online.gallery.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class PasswordUpdateRequest {
    @JsonProperty("old_password")
    private String oldPassword;
    @Schema(name = "password", example = "password123", minLength = 8, maxLength = 30)
    @Length(min = 8, max = 30, message = "password must contain more than 8 characters and less than 30 characters")
    @NotNull(message = "Password is empty")
    @Pattern(regexp = "^(?=.*[0-9])(?=\\S+$).{8,}$", message = "the password must contain at least 1 digit and be without spaces")
    @JsonProperty("new_password")
    private String newPassword;
}
