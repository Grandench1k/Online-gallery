package com.online.gallery.controller.user;

import com.online.gallery.dto.request.PasswordUpdateRequest;
import com.online.gallery.dto.response.BadRequestExceptionResponse;
import com.online.gallery.dto.response.NotFoundExceptionResponse;
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

@Tag(name = "UserController",
        description = "Controller for manage users and their profile images")
@SecurityRequirement(name = "Authorization")
public interface UserController {

    @Operation(summary = "GET profile image",
            description = "GET profile image by userId",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(mediaType = "image/jpeg")),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found any profile images.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = NotFoundExceptionResponse.class)))
            })
    ResponseEntity<byte[]> getUserProfileImage(Authentication authentication);

    @Operation(summary = "POST profile image",
            description = "POST profile image by userId",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = OkResponse.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Incorrect profile image format," +
                                    " please send image with .jpg, .png, .webp and .jpeg formats.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BadRequestExceptionResponse.class)))
            })
    ResponseEntity<OkResponse> saveUserProfileImage(
            MultipartFile profileImageFile,
            Authentication authentication) throws IOException;

    @Operation(summary = "UPDATE profile image by Id",
            description = "UPDATE profile image by UserId",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = OkResponse.class))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found any profile images.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = NotFoundExceptionResponse.class)))
            })
    ResponseEntity<OkResponse> updateUserProfileImage(
            MultipartFile profileImageFile,
            Authentication authentication) throws IOException;

    @Operation(summary = "UPDATE password",
            description = "UPDATE user password",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = OkResponse.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid old password",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BadRequestExceptionResponse.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "passwords match.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BadRequestExceptionResponse.class)))
            })
    ResponseEntity<OkResponse> updateUserPassword(
            PasswordUpdateRequest passwordUpdateRequest,
            Authentication authentication);
}
