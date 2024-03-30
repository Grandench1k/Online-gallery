package com.online.gallery.controller;

import com.online.gallery.dto.request.AuthenticationRequest;
import com.online.gallery.dto.request.PasswordResetRequest;
import com.online.gallery.dto.request.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import com.online.gallery.dto.response.*;
import com.online.gallery.model.PasswordResetConfirmationRequest;

import java.io.IOException;

@Tag(name = "AuthenticationController", description = "Controller for authentication")
public interface AuthenticationController {
    @Operation(summary = "sign up",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class))),
                    @ApiResponse(responseCode = "409", description = "this user already exist.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class))),
                    @ApiResponse(responseCode = "409", description = "this user already exist, but not registered with email. Try again in 15 minutes.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class)))})
    ResponseEntity<AuthenticationResponse> signUp(RegisterRequest request) throws MessagingException;

    @Operation(summary = "activate user", description = "Confirm user's sign up by email",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OkResponse.class))),
                    @ApiResponse(responseCode = "400", description = "please confirm your registration with email.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class))),
                    @ApiResponse(responseCode = "400", description = "confirmation token is expired.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class))),
                    @ApiResponse(responseCode = "404", description = "user not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundResponse.class))),
                    @ApiResponse(responseCode = "404", description = "confirmation token not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundResponse.class)))})
    ResponseEntity<OkResponse> activateUser(String token);

    @Operation(summary = "sign in",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class))),
                    @ApiResponse(responseCode = "400", description = "please confirm your registration with email.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class))),
                    @ApiResponse(responseCode = "404", description = "user not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundResponse.class)))})
    ResponseEntity<AuthenticationResponse> signIn(AuthenticationRequest request);


    @Operation(summary = "refresh access token",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class))),})
    void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException;

    @Operation(summary = "start reset password", description = "send password reset link on user's email",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OkResponse.class))),
                    @ApiResponse(responseCode = "400", description = "please confirm your registration with email.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class))),
                    @ApiResponse(responseCode = "404", description = "user not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundResponse.class)))})
    ResponseEntity<OkResponse> forgotPassword(PasswordResetRequest passwordResetRequest) throws MessagingException;

    @Operation(summary = "page for reset password", description = "page for users to reset passwords",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OkResponse.class))),
                    @ApiResponse(responseCode = "400", description = "please confirm your registration with email.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class))),
                    @ApiResponse(responseCode = "400", description = "password reset token is expired.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class))),
                    @ApiResponse(responseCode = "404", description = "user not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OtherExceptionsResponse.class))),
                    @ApiResponse(responseCode = "404", description = "password reset token not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OtherExceptionsResponse.class)))})
    ResponseEntity<OkResponse> confirmPasswordResetPage(String token);

    @Operation(summary = "password reset", description = "reset user's password",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PasswordResetConfirmationRequest.class))),
                    @ApiResponse(responseCode = "400", description = "please confirm your registration with email.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class))),
                    @ApiResponse(responseCode = "400", description = "password reset token is expired.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class))),
                    @ApiResponse(responseCode = "404", description = "user not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OtherExceptionsResponse.class))),
                    @ApiResponse(responseCode = "404", description = "password reset token not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OtherExceptionsResponse.class)))})
    ResponseEntity<OkResponse> confirmPasswordReset(
            String token,
            PasswordResetConfirmationRequest passwordResetConfirmationRequest);
}