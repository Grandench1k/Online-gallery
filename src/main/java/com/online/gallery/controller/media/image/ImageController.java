package com.online.gallery.controller.media.image;

import com.online.gallery.dto.response.ExceptionResponse;
import com.online.gallery.model.media.Image;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@SecurityRequirement(name = "Authorization")
@Tag(name = "Image controller", description = "controller for managing images")
public interface ImageController {
    @Operation(summary = "list all images",
            description = "retrieves all images associated with the authenticated user")
    @ApiResponse(responseCode = "200", description = "images retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = List.class)))
    @ApiResponse(responseCode = "404", description = "no images found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<List<Image>> listAllImages(Authentication authentication);

    @Operation(summary = "get image by id",
            description = "retrieves a specific image by its id for the authenticated user")
    @ApiResponse(responseCode = "200", description = "image retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Image.class)))
    @ApiResponse(responseCode = "404", description = "image not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<byte[]> getImageById(String id, Authentication authentication);

    @Operation(summary = "save image",
            description = "saves an image metadata and returns the updated image data")
    @ApiResponse(responseCode = "200", description = "image saved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Image.class)))
    ResponseEntity<Image> saveImage(
            Image image,
            MultipartFile imageFile,
            Authentication authentication) throws IOException;

    @Operation(summary = "update image by id",
            description = "updates a specific image for the authenticated user")
    @ApiResponse(responseCode = "200", description = "image updated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Image.class)))
    @ApiResponse(responseCode = "404", description = "image with this id not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<Image> updateImageById(
            String id,
            Image image,
            Authentication authentication);

    @Operation(summary = "delete user image by id",
            description = "deletes a specific image for the authenticated user")
    @ApiResponse(responseCode = "200", description = "image deleted successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Image.class)))
    @ApiResponse(responseCode = "404", description = "image with this id not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<Image> deleteImageById(@PathVariable String id, Authentication authentication);
}
