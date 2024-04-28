package com.online.gallery.controller.media.image;

import com.online.gallery.dto.request.ImageDetailsRequest;
import com.online.gallery.dto.request.ImageFileUploadRequest;
import com.online.gallery.dto.response.DataResponse;
import com.online.gallery.dto.response.ExceptionResponse;
import com.online.gallery.dto.response.PresignedLinkResponse;
import com.online.gallery.entity.media.Image;
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
@Tag(name = "Image controller", description = "controller for managing images")
public interface ImageController {

    @Operation(summary = "list all images",
            description = "retrieves all images associated with the authenticated user")
    @ApiResponse(responseCode = "200", description = "images retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DataResponse.class)))
    @ApiResponse(responseCode = "404", description = "no images found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<DataResponse<List<Image>>> listAllImages(User user);

    @Operation(summary = "get image by id",
            description = "retrieves a specific image by its id for the authenticated user")
    @ApiResponse(responseCode = "200", description = "image retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DataResponse.class)))
    @ApiResponse(responseCode = "404", description = "image not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<DataResponse<Image>> getImageById(@PathVariable String imageId,
                                                     User user);

    @Operation(summary = "generate presigned get image url",
            description = "generates a presigned URL for getting an image from S3")
    @ApiResponse(responseCode = "200", description = "presigned URL generated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PresignedLinkResponse.class)))
    ResponseEntity<PresignedLinkResponse> generatePresignedGetImageUrl(@PathVariable String imageId,
                                                                       User user);

    @Operation(summary = "prepare image upload",
            description = "generates a presigned URL for uploading an image to S3")
    @ApiResponse(responseCode = "200", description = "presigned upload URL generated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PresignedLinkResponse.class)))
    ResponseEntity<PresignedLinkResponse> prepareImageUpload(
            @RequestBody @Valid ImageFileUploadRequest imageFileUploadRequest,
            User user);

    @Operation(summary = "save image",
            description = "saves an image metadata and returns the updated image data")
    @ApiResponse(responseCode = "200", description = "image saved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DataResponse.class)))
    ResponseEntity<DataResponse<Image>> saveImage(@RequestBody @Valid Image image,
                                                  User user);

    @Operation(summary = "update user image details",
            description = "updates details of a specific image for the authenticated user")
    @ApiResponse(responseCode = "200", description = "image updated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DataResponse.class)))
    @ApiResponse(responseCode = "404", description = "image with this id not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<DataResponse<Image>> updateImageDetailsById(
            @RequestBody @Valid ImageDetailsRequest imageDetailsRequest,
            @PathVariable String imageId,
            User user);

    @Operation(summary = "delete user image by id",
            description = "deletes a specific image for the authenticated user")
    @ApiResponse(responseCode = "200", description = "image deleted successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DataResponse.class)))
    @ApiResponse(responseCode = "404", description = "image with this id not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<DataResponse<Image>> deleteImageById(@PathVariable String imageId,
                                                        User user);
}

