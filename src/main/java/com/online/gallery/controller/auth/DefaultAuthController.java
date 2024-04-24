package com.online.gallery.controller.auth;

import com.online.gallery.dto.request.PasswordResetCompleteRequest;
import com.online.gallery.dto.request.PasswordResetStartRequest;
import com.online.gallery.dto.request.SignInRequest;
import com.online.gallery.dto.request.SignUpRequest;
import com.online.gallery.dto.response.AuthTokenResponse;
import com.online.gallery.dto.response.MessageResponse;
import com.online.gallery.service.auth.AuthService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
public class DefaultAuthController implements AuthController {
    private final AuthService authService;

    public DefaultAuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthTokenResponse> processSignUp(
            @RequestBody @Valid SignUpRequest request) throws MessagingException {
        return ResponseEntity
                .ok(authService.processSignUp(request));
    }

    @GetMapping("signup/{token}")
    public ResponseEntity<MessageResponse> completeSignUp(@PathVariable String token) {
        return ResponseEntity
                .ok(new MessageResponse(authService.completeSignUp(token)));
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthTokenResponse> signIn(
            @RequestBody @Valid SignInRequest request) {
        return ResponseEntity
                .ok(authService.signIn(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logOut() {
        SecurityContextHolder.clearContext();
        return ResponseEntity
                .ok(new MessageResponse("successful logout"));
    }

    @PostMapping("/refresh")
    public void refreshToken(HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        authService.refreshToken(request, response);
    }

    @PostMapping("password")
    public ResponseEntity<MessageResponse> forgotPassword(
            @RequestBody @Valid PasswordResetStartRequest passwordResetStartRequest
    ) throws MessagingException {
        return ResponseEntity
                .ok(new MessageResponse(authService.sendMessageForResetPassword(passwordResetStartRequest.getEmail())));
    }

    @GetMapping("password/{token}")
    public ResponseEntity<MessageResponse> startPasswordReset(@PathVariable String token) {
        return ResponseEntity
                .ok(new MessageResponse(authService.processPasswordReset(token)));
    }

    @PostMapping("password/{token}")
    public ResponseEntity<MessageResponse> completePasswordReset(
            @PathVariable String token,
            @RequestBody @Valid PasswordResetCompleteRequest passwordResetCompleteRequest) {
        return ResponseEntity
                .ok(new MessageResponse(authService.completePasswordReset(token,
                        passwordResetCompleteRequest.getPassword())));
    }
}
