package com.online.gallery.controller.media.video;

import com.online.gallery.dto.response.DataResponse;
import com.online.gallery.dto.response.ExceptionResponse;
import com.online.gallery.model.media.Video;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@SecurityRequirement(name = "Authorization")
@Tag(name = "Video controller", description = "controller for managing videos")
public interface VideoController {
    @Operation(summary = "list all videos",
            description = "retrieves all videos associated with the authenticated user")
    @ApiResponse(responseCode = "200", description = "videos retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DataResponse.class)))
    @ApiResponse(responseCode = "404", description = "no videos found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<DataResponse<List<Video>>> listAllVideos(Authentication authentication);

    @Operation(summary = "get video by id",
            description = "retrieves a specific video by its id for the authenticated user")
    @ApiResponse(responseCode = "200", description = "video retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DataResponse.class)))
    @ApiResponse(responseCode = "404", description = "video not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<DataResponse<byte[]>> getVideoById(String id, Authentication authentication);

    @Operation(summary = "save video",
            description = "saves video metadata and returns the updated video data")
    @ApiResponse(responseCode = "200", description = "video saved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DataResponse.class)))
    ResponseEntity<DataResponse<Video>> saveVideo(
            Video video,
            MultipartFile videoFile,
            Authentication authentication) throws IOException;

    @Operation(summary = "update video details",
            description = "updates details of a specific video for the authenticated user")
    @ApiResponse(responseCode = "200", description = "video updated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DataResponse.class)))
    @ApiResponse(responseCode = "404", description = "video with this id not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<DataResponse<Video>> updateVideoById(String id,
                                                        Video video,
                                                        Authentication authentication);

    @Operation(summary = "delete video by id",
            description = "deletes a specific video for the authenticated user")
    @ApiResponse(responseCode = "200", description = "video deleted successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DataResponse.class)))
    @ApiResponse(responseCode = "404", description = "video with this id not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<DataResponse<Video>> deleteVideoById(String id, Authentication authentication);
}