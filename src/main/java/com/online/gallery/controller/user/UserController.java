package com.online.gallery.controller.user;

import com.online.gallery.dto.request.ImageFileUploadRequest;
import com.online.gallery.dto.request.PasswordUpdateRequest;
import com.online.gallery.dto.response.DataResponse;
import com.online.gallery.dto.response.ExceptionResponse;
import com.online.gallery.dto.response.MessageResponse;
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

@SecurityRequirement(name = "Authorization")
@Tag(name = "User controller", description =
        "controller for managing user profiles and their associated operations")
public interface UserController {

    ResponseEntity<DataResponse<User>> getUser(User user);

    @Operation(summary = "get user profile image",
            description = "retrieves a presigned URL to download the current user's profile image")
    @ApiResponse(responseCode = "200", description = "profile image URL retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PresignedLinkResponse.class)))
    @ApiResponse(responseCode = "404", description = "no profile image found for the user",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<PresignedLinkResponse> getProfileImage(User user);

    @Operation(summary = "prepare profile image upload",
            description = "generates a presigned URL for uploading a new profile image for the current user")
    @ApiResponse(responseCode = "200", description = "upload URL generated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PresignedLinkResponse.class)))
    @ApiResponse(responseCode = "400", description = "incorrect or unsupported image format specified",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<PresignedLinkResponse> prepareProfileImageUpload(
            @RequestBody @Valid ImageFileUploadRequest imageFileUploadRequest,
            User user);

    @Operation(summary = "save profile image",
            description = "saves an profile image metadata and returns the updated user data")
    @ApiResponse(responseCode = "200", description = "update URL generated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DataResponse.class)))
    @ApiResponse(responseCode = "404", description = "no existing profile image found to update",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<DataResponse<User>> saveProfileImage(
            @RequestBody @Valid Image image,
            User user);

    @Operation(summary = "update user password",
            description = "updates the user's password after validating the old password")
    @ApiResponse(responseCode = "200", description = "password updated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MessageResponse.class)))
    @ApiResponse(responseCode = "400", description = "invalid old password provided or new password " +
            "matches the old password",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<MessageResponse> updatePassword(
            @RequestBody @Valid PasswordUpdateRequest passwordUpdateRequest,
            User user);
}


