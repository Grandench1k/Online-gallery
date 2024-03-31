package com.online.gallery.model;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Getter
@Setter
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

    public Video(String id, String name, String uri, String userId) {
        this.id = id;
        this.name = name;
        this.uri = uri;
        this.userId = userId;
    }
}
