package com.online.gallery.controller.auth;

import com.online.gallery.dto.request.SignInRequest;
import com.online.gallery.dto.request.PasswordResetStartRequest;
import com.online.gallery.dto.request.SignUpRequest;
import com.online.gallery.dto.response.AuthTokenResponse;
import com.online.gallery.dto.response.OkResponse;
import com.online.gallery.dto.request.PasswordResetCompleteRequest;
import com.online.gallery.service.auth.AuthenticationService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationControllerImpl implements AuthenticationController {
    private final AuthenticationService service;

    public AuthenticationControllerImpl(AuthenticationService service) {
        this.service = service;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthTokenResponse> signUp(@RequestBody @Validated SignUpRequest request) throws MessagingException {
        return ResponseEntity.ok(service.register(request));
    }

    @GetMapping("/signup/{token}")
    public ResponseEntity<OkResponse> activateUser(@PathVariable String token) {
        return ResponseEntity.ok(new OkResponse(service.activate(token)));
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthTokenResponse> signIn(@RequestBody @Validated SignInRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<OkResponse> logOut() {
        SecurityContextHolder.clearContext();
        return ResponseEntity
                .ok(new OkResponse("successful logout"));
    }

    @PostMapping("/refresh")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        service.refreshToken(request, response);
    }

    @PostMapping("password")
    public ResponseEntity<OkResponse> forgotPassword(@RequestBody @Validated PasswordResetStartRequest passwordResetStartRequest) throws MessagingException {
        return ResponseEntity.ok(new OkResponse(service.sendMessageForReset(passwordResetStartRequest.getEmail())));
    }

    @GetMapping("password/{token}")
    public ResponseEntity<OkResponse> confirmPasswordResetPage(@PathVariable String token) {
        return ResponseEntity.ok(new OkResponse(service.checkPasswordResetTokenAndUser(token)));
    }

    @PostMapping("password/{token}")
    public ResponseEntity<OkResponse> confirmPasswordReset(
            @PathVariable String token,
            @RequestBody @Validated PasswordResetCompleteRequest passwordResetCompleteRequest) {
        return ResponseEntity.ok(new OkResponse(service.resetPassword(token, passwordResetCompleteRequest.getPassword())));
    }
}
