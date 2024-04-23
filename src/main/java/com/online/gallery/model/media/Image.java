package com.online.gallery.model.media;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Getter
@Setter
@Document(collection = "images")
public class Image implements Serializable {
    @Id
    @Hidden
    private String id;
    @Schema(
            name = "name",
            example = "name",
            defaultValue = "name",
            minLength = 2,
            maxLength = 20
    )
    @NotNull(message = "name is empty")
    @Length(
            min = 2,
            max = 20,
            message = "name of image must contain more than 2 characters and less than 20 characters"
    )
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
