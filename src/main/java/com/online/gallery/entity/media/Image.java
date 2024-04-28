package com.online.gallery.entity.media;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Getter
@Setter
@Document(collection = "images")
@Schema(description = "represents an image")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Image implements Serializable {

    @Id
    @Schema(
            description = "mongoDB ObjectId",
            example = "507f1f77bcf86cd799439011",
            pattern = "^[0-9a-fA-F]{24}$",
            type = "string"
    )
    @Pattern(regexp = "^[0-9a-fA-F]{24}$", message = "Invalid ObjectId")
    private String id;

    @Schema(description = "name of the image", example = "sunset photo", minLength = 1, maxLength = 20)
    @Length(min = 1, max = 20, message = "name must contain between 1 and 20 characters")
    @NotNull(message = "name is required")
    private String name;

    @Hidden
    private String filePath;

    @Hidden
    private String userId;

    public Image(String id, String name, String filePath, String userId) {
        this.id = id;
        this.name = name;
        this.filePath = filePath;
        this.userId = userId;
    }
}
