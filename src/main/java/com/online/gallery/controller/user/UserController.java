package com.online.gallery.controller.user;

import com.online.gallery.dto.request.PasswordUpdateRequest;
import com.online.gallery.dto.response.ExceptionResponse;
import com.online.gallery.dto.response.OkResponse;
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

@SecurityRequirement(name = "Authorization")
@Tag(name = "User controller", description =
        "controller for managing user profiles and their associated operations")
public interface UserController {

    @Operation(summary = "get user profile image",
            description = "retrieves a presigned URL to download the current user's profile image")
    @ApiResponse(responseCode = "200", description = "profile image URL retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Byte.class)))
    @ApiResponse(responseCode = "404", description = "no profile image found for the user",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<byte[]> getProfileImage(Authentication authentication);

    @Operation(summary = "save profile image",
            description = "saves an profile image and returns the updated user data")
    @ApiResponse(responseCode = "200", description = "update URL generated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OkResponse.class)))
    @ApiResponse(responseCode = "404", description = "no existing profile image found to update",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<OkResponse> saveProfileImage(
            MultipartFile profileImageFile,
            Authentication authentication) throws IOException;

    @Operation(summary = "update profile image",
            description = "updates an profile image and returns the updated user data")
    @ApiResponse(responseCode = "200", description = "update URL generated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OkResponse.class)))
    @ApiResponse(responseCode = "404", description = "no existing profile image found to update",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<OkResponse> updateProfileImage(
            MultipartFile profileImageFile,
            Authentication authentication) throws IOException;

    @Operation(summary = "update user password",
            description = "updates the user's password after validating the old password")
    @ApiResponse(responseCode = "200", description = "password updated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OkResponse.class)))
    @ApiResponse(responseCode = "400", description = "invalid old password provided or new password " +
            "matches the old password",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<OkResponse> updatePassword(
            PasswordUpdateRequest passwordUpdateRequest,
            Authentication authentication);
}
