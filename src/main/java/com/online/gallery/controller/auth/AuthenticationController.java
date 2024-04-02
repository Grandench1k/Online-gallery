package com.online.gallery.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import com.online.gallery.dto.request.PasswordResetCompleteRequest;
import com.online.gallery.dto.request.PasswordResetStartRequest;
import com.online.gallery.dto.request.SignUpRequest;
import com.online.gallery.dto.request.SignInRequest;
import com.online.gallery.dto.response.*;

import java.io.IOException;

@Tag(name = "AuthenticationController",
        description = "Controller for authentication")
public interface AuthenticationController {
    @Operation(summary = "sign up",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AuthTokenResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "this user already exist.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BadRequestExceptionResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "this user already exist," +
                                    " but not registered with email. Try again in 15 minutes.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BadRequestExceptionResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<AuthTokenResponse> startSignUp(
            SignUpRequest request) throws MessagingException;

    @Operation(summary = "activate user",
            description = "Confirm user's sign up by email",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = OkResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "please confirm your registration with email.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BadRequestExceptionResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "confirmation token is expired.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BadRequestExceptionResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "user not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = NotFoundExceptionResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "confirmation token not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = NotFoundExceptionResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<OkResponse> completeSignUp(String token);

    @Operation(summary = "sign in",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AuthTokenResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "please confirm your registration with email.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BadRequestExceptionResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "user not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = NotFoundExceptionResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<AuthTokenResponse> signIn(SignInRequest request);

    ResponseEntity<OkResponse> logOut();


    @Operation(summary = "refresh access token",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AuthTokenResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Full authentication is required to access this resource.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BadRequestExceptionResponse.class)
                            )
                    ),
            }
    )
    void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException;

    @Operation(summary = "start reset password",
            description = "send password reset link on user's email",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = OkResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "please confirm your registration with email.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BadRequestExceptionResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "user not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = NotFoundExceptionResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<OkResponse> forgotPassword(
            PasswordResetStartRequest passwordResetStartRequest) throws MessagingException;

    @Operation(summary = "page for reset password",
            description = "page for users to reset passwords",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = OkResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "please confirm your registration with email.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BadRequestExceptionResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "password reset token is expired.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BadRequestExceptionResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "user not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DefaultExceptionResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "password reset token not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DefaultExceptionResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<OkResponse> passwordResetPage(String token);

    @Operation(summary = "password reset",
            description = "reset user's password",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PasswordResetCompleteRequest.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "please confirm your registration with email.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BadRequestExceptionResponse.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "password reset token is expired.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BadRequestExceptionResponse.class))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "user not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DefaultExceptionResponse.class))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "password reset token not found.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DefaultExceptionResponse.class)))
            })
    ResponseEntity<OkResponse> completePasswordReset(
            String token,
            PasswordResetCompleteRequest passwordResetCompleteRequest);
}
