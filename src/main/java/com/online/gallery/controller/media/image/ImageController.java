package com.online.gallery.controller.media.image;

import com.online.gallery.dto.response.BadRequestExceptionResponse;
import com.online.gallery.dto.response.DefaultExceptionResponse;
import com.online.gallery.dto.response.NotFoundExceptionResponse;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "ImageController",
        description = "Controller for manage images")
@SecurityRequirement(name = "Authorization")
public interface ImageController {
    @Operation(summary = "GET all images",
            description = "GET all images by userId",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Image.class))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Images not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = NotFoundExceptionResponse.class)))
            })
    ResponseEntity<List<Image>> getAllImages(Authentication authentication);

    @Operation(summary = "GET image by Id",
            description = "GET image by Id and UserId",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(mediaType = "image/png")),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Image with this id not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = NotFoundExceptionResponse.class)))
            })
    ResponseEntity<byte[]> getImageById(@PathVariable String id, Authentication authentication);

    @Operation(summary = "POST image",
            description = "POST image by userId",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Image.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Incorrect image format, please send " +
                                    "image with .jpg, .jpeg, .png, .webp, .bmp, .gif and .tiff formats.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BadRequestExceptionResponse.class))),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Image with this name is already defined.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BadRequestExceptionResponse.class)))
            })
    ResponseEntity<Image> saveImage(
            Image image,
            MultipartFile imageFile,
            Authentication authentication) throws IOException;

    @Operation(summary = "UPDATE image by Id",
            description = "UPDATE image by Id and UserId",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Image.class))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Image with this id not found.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BadRequestExceptionResponse.class))),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Image with this name is already defined.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BadRequestExceptionResponse.class)))
            })
    ResponseEntity<Image> updateImageById(
            @PathVariable String id,
            @RequestBody Image image,
            Authentication authentication);

    @Operation(summary = "DELETE image by Id",
            description = "DELETE image by Id and UserId",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Image.class))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Images not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DefaultExceptionResponse.class)))
            })
    ResponseEntity<Image> deleteImageById(@PathVariable String id, Authentication authentication);
}
