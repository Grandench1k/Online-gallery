package com.online.gallery.model;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Document(collection = "videos")
public class Video implements Serializable {
    @Id
    @Hidden
    private String id;
    @Schema(name = "name", example = "name", defaultValue = "name", minLength = 2, maxLength = 20)
    @NotEmpty(message = "Name is empty")
    @Length(min = 2, max = 20, message = "name of video must contain more than 2 characters and less than 20 characters")
    private String name;
    @Hidden
    private String uri;
    @Hidden
    private String userId;
}
