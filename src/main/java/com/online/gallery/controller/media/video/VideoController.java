package com.online.gallery.controller.media.video;

import com.online.gallery.dto.request.VideoDetailsRequest;
import com.online.gallery.dto.request.VideoFileUploadRequest;
import com.online.gallery.dto.response.DataResponse;
import com.online.gallery.dto.response.ExceptionResponse;
import com.online.gallery.dto.response.PresignedLinkResponse;
import com.online.gallery.entity.media.Video;
import com.online.gallery.entity.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

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
    ResponseEntity<DataResponse<List<Video>>> listAllVideos(User user);

    @Operation(summary = "get video by id",
            description = "retrieves a specific video by its id for the authenticated user")
    @ApiResponse(responseCode = "200", description = "video retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DataResponse.class)))
    @ApiResponse(responseCode = "404", description = "video not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<DataResponse<Video>> getVideoById(@PathVariable String videoId,
                                                     User user);

    @Operation(summary = "generate presigned get video url",
            description = "generates a presigned URL for getting a video from S3")
    @ApiResponse(responseCode = "200", description = "presigned URL generated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PresignedLinkResponse.class)))
    ResponseEntity<PresignedLinkResponse> generatePresignedGetVideoUrl(@PathVariable String videoId,
                                                                       User user);

    @Operation(summary = "prepare video upload",
            description = "generates a presigned URL for uploading a video to S3")
    @ApiResponse(responseCode = "200", description = "presigned upload URL generated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PresignedLinkResponse.class)))
    ResponseEntity<PresignedLinkResponse> prepareVideoUpload(
            @RequestBody @Valid VideoFileUploadRequest videoFileUploadRequest,
            User user);

    @Operation(summary = "save video",
            description = "saves video metadata and returns the updated video data")
    @ApiResponse(responseCode = "200", description = "video saved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DataResponse.class)))
    ResponseEntity<DataResponse<Video>> saveVideo(@RequestBody @Valid Video video,
                                                  User user);

    @Operation(summary = "update video by id",
            description = "updates video for the authenticated user")
    @ApiResponse(responseCode = "200", description = "video updated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DataResponse.class)))
    @ApiResponse(responseCode = "404", description = "video with this id not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<DataResponse<Video>> updateVideoById(
            @RequestBody @Valid Video video,
            @PathVariable String videoId,
            User user);

    @Operation(summary = "update video details",
            description = "updates details of a specific video for the authenticated user")
    @ApiResponse(responseCode = "200", description = "video updated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DataResponse.class)))
    @ApiResponse(responseCode = "404", description = "video with this id not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<DataResponse<Video>> updateVideoDetailsById(
            @RequestBody @Valid VideoDetailsRequest videoDetailsRequest,
            @PathVariable String videoId,
            User user);

    @Operation(summary = "delete video by id",
            description = "deletes a specific video for the authenticated user")
    @ApiResponse(responseCode = "200", description = "video deleted successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DataResponse.class)))
    @ApiResponse(responseCode = "404", description = "video with this id not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<DataResponse<Video>> deleteVideoById(@PathVariable String videoId,
                                                        User user);
}


