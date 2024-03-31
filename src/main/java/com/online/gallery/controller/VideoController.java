package com.online.gallery.controller;

import com.online.gallery.dto.response.BadRequestResponse;
import com.online.gallery.dto.response.NotFoundResponse;
import com.online.gallery.model.Video;
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

@Tag(name = "VideoController", description = "Controller for manage videos")
@SecurityRequirement(name = "Authorization")
public interface VideoController {
    @Operation(summary = "GET all videos", description = "GET all videos by userId",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Video.class))),
                    @ApiResponse(responseCode = "404", description = "Videos not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundResponse.class)))})
    ResponseEntity<List<Video>> findAllVideos(Authentication authentication);

    @Operation(summary = "GET video by Id", description = "GET video by Id and UserId",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(mediaType = "video/mp4")),
                    @ApiResponse(responseCode = "404", description = "Video with this id not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundResponse.class)))})
    ResponseEntity<byte[]> findVideoById(String id, Authentication authentication);

    @Operation(summary = "POST video", description = "POST video by userId",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Video.class))),
                    @ApiResponse(responseCode = "400", description = "Incorrect video format, please send video with .mp4, .mpeg and .ogg formats.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class))),
                    @ApiResponse(responseCode = "409", description = "Video with this name is already defined.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundResponse.class)))})
    ResponseEntity<Video> saveVideo(Video video, MultipartFile videoFile, Authentication authentication) throws IOException;

    @Operation(summary = "UPDATE video by Id", description = "UPDATE video by Id and UserId",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Video.class))),
                    @ApiResponse(responseCode = "404", description = "Video with this id not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundResponse.class))),
                    @ApiResponse(responseCode = "409", description = "Video with this name is already defined.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class)))})
    ResponseEntity<Video> updateVideoById(String id, Video video, Authentication authentication);

    @Operation(summary = "DELETE video by Id", description = "DELETE video by Id and UserId",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Video.class))),
                    @ApiResponse(responseCode = "404", description = "Videos not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundResponse.class)))})
    ResponseEntity<Video> deleteVideoById(String id, Authentication authentication);
}
