package com.online.gallery.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoFileUploadRequest {

    @Schema(
            description = "mongoDB ObjectId",
            example = "507f1f77bcf86cd799439011",
            pattern = "^[0-9a-fA-F]{24}$",
            type = "string"
    )
    @Pattern(regexp = "^[0-9a-fA-F]{24}$", message = "invalid ObjectId")
    @NotNull(message = "image ID is empty")
    private String id;

    @Schema(name = "name of file", example = "holiday_video.mp4", minLength = 1, maxLength = 255)
    @Size(min = 1, max = 255, message = "file name must not exceed 255 characters")
    @NotNull(message = "name of file is empty")
    private String name;

    @Pattern(regexp = "video/mp4|video/x-msvideo|video/x-ms-wmv|video/quicktime|video/x-matroska|video" +
            "/webm|video/3gpp|video/3gpp2|video/ogg|video/mpeg",
            message = "unsupported video content type")
    @NotNull(message = "contentType is empty")
    private String contentType;

    @DecimalMin(value = "102400", message = "the minimum size of the video should be 100 KB")
    @DecimalMax(value = "1073741824", message = "the maximum size of the video should be 1 GB")
    @NotNull(message = "size of file is empty")
    private long size;
}
