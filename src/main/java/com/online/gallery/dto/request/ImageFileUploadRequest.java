package com.online.gallery.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageFileUploadRequest {
    @Schema(
            description = "mongoDB ObjectId",
            example = "507f1f77bcf86cd799439011",
            pattern = "^[0-9a-fA-F]{24}$",
            type = "string"
    )
    @Pattern(regexp = "^[0-9a-fA-F]{24}$", message = "invalid ObjectId")
    @NotNull(message = "image ID is empty")
    private String id;

    @Schema(
            name = "name of file",
            example = "vacation_photo.jpeg",
            minLength = 1,
            maxLength = 255,
            type = "string"
    )
    @Size(min = 1, max = 255, message = "file name must not exceed 255 characters")
    @NotNull(message = "file name is empty")
    private String name;

    @Pattern(regexp = "image/png|image/jpeg|image/gif|image/bmp|image/svg\\+xml|image/" +
            "webp|image/heif|image/heic|image/tiff",
            message = "unsupported content type")
    @NotNull(message = "content type is empty")
    private String contentType;

    @DecimalMin(value = "10240", message = "the minimum size of the image should be 10 KB")
    @DecimalMax(value = "20971520", message = "the maximum size of the image should be 20 MB")
    @NotNull(message = "size of file is empty")
    private long size;
}
