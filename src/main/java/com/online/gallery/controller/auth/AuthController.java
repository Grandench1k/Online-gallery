package com.online.gallery.controller.auth;

import com.online.gallery.dto.request.PasswordResetCompleteRequest;
import com.online.gallery.dto.request.PasswordResetStartRequest;
import com.online.gallery.dto.request.SignInRequest;
import com.online.gallery.dto.request.SignUpRequest;
import com.online.gallery.dto.response.AuthTokenResponse;
import com.online.gallery.dto.response.ExceptionResponse;
import com.online.gallery.dto.response.OkResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

@Tag(name = "AuthController",
        description = "Controller for authentication")
public interface AuthController {
    @Operation(summary = "Sign up",
            description = "registers a new user with the provided credentials and sends a "
                    + "confirmation email. Requires confirmation of the email to activate the account")
    @ApiResponse(responseCode = "200", description = "user registered successfully and "
            + "confirmation email sent",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AuthTokenResponse.class)))
    @ApiResponse(responseCode = "409", description = "user with this email already exists "
            + "or confirmation token already sent and not expired",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    @ApiResponse(responseCode = "500", description = "messaging exception occurred",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<AuthTokenResponse> startSignUp(
            SignUpRequest request) throws MessagingException;

    @Operation(summary = "Complete sign up",
            description = "completes the registration process by verifying the provided token "
                    + "sent via email")
    @ApiResponse(responseCode = "200", description = "account successfully activated",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AuthTokenResponse.class)))
    @ApiResponse(responseCode = "400", description = "token invalid or expired",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    @ApiResponse(responseCode = "404", description = "token or user not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<OkResponse> completeSignUp(String token);

    @Operation(summary = "Sign in",
            description = "authenticates the user with provided credentials and returns "
                    + "authentication tokens")
    @ApiResponse(responseCode = "200", description = "successfully authenticated and tokens "
            + "returned",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AuthTokenResponse.class)))
    @ApiResponse(responseCode = "400", description = "invalid credentials or account not "
            + "activated",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    @ApiResponse(responseCode = "404", description = "user not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<AuthTokenResponse> signIn(SignInRequest request);

    @Operation(summary = "Log out",
            description = "logs out the current user by clearing the security context")
    @ApiResponse(responseCode = "200", description = "successfully logged out",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OkResponse.class)))
    ResponseEntity<OkResponse> logOut();


    @Operation(summary = "Refresh access token",
            description = "refreshes the user's authentication tokens by validating the "
                    + "provided refresh token")
    @ApiResponse(responseCode = "200", description = "access token refreshed successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AuthTokenResponse.class)))
    @ApiResponse(responseCode = "401", description = "unauthorized access or invalid refresh "
            + "token",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    @SecurityRequirement(name = "Authorization")
    void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException;

    @Operation(summary = "Start password reset",
            description = "initiates the password reset process by sending a reset link to the "
                    + "user's email")
    @ApiResponse(responseCode = "200", description = "password reset link sent successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OkResponse.class)))
    @ApiResponse(responseCode = "400", description = "account not activated or email not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    @ApiResponse(responseCode = "404", description = "user not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<OkResponse> forgotPassword(
            PasswordResetStartRequest passwordResetStartRequest) throws MessagingException;

    @Operation(summary = "Start password reset",
            description = "initiates the password reset process by sending a reset link to the "
                    + "user's email")
    @ApiResponse(responseCode = "200", description = "password reset link sent successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OkResponse.class)))
    @ApiResponse(responseCode = "400", description = "account not activated or email not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    @ApiResponse(responseCode = "404", description = "user not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<OkResponse> passwordResetPage(String token);

    @Operation(summary = "Complete password reset",
            description = "completes the password reset process using the provided token and "
                    + "new password")
    @ApiResponse(responseCode = "200", description = "password reset successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OkResponse.class)))
    @ApiResponse(responseCode = "400", description = "invalid or expired token, or passwords "
            + "match the old password",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    @ApiResponse(responseCode = "404", description = "token or user not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExceptionResponse.class)))
    ResponseEntity<OkResponse> completePasswordReset(
            String token,
            PasswordResetCompleteRequest passwordResetCompleteRequest);
}
