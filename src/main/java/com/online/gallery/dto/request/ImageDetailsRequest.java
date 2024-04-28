package com.online.gallery.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class ImageDetailsRequest {
    @Schema(description = "name of the image", example = "sunset photo", minLength = 1, maxLength = 20)
    @Length(min = 1, max = 20, message = "name must contain between 1 and 20 characters")
    @NotNull(message = "name is required")
    private String name;
}
