package com.online.gallery.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PasswordUpdateRequest {
    @NotNull(message = "old password is empty")
    private String oldPassword;

    @Schema(name = "password", example = "NewSecure1234", minLength = 8, maxLength = 30)
    @Size(min = 8, max = 30, message = "new password must contain between 8 characters and 30 characters")
    @NotNull(message = "new password is empty")
    @Pattern(regexp = "^(?=.*[0-9])(?=\\S+$).{8,}$", message = "the password must contain at least 1 digit " +
            "and be without spaces")
    private String newPassword;
}
