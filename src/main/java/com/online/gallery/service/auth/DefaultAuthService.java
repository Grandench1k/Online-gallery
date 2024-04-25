package com.online.gallery.service.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.online.gallery.dto.request.SignInRequest;
import com.online.gallery.dto.request.SignUpRequest;
import com.online.gallery.dto.response.AuthTokenResponse;
import com.online.gallery.exception.auth.TokenDuplicationException;
import com.online.gallery.exception.auth.TokenExpirationException;
import com.online.gallery.exception.auth.TokenInvalidException;
import com.online.gallery.exception.auth.TokenNotFoundException;
import com.online.gallery.exception.user.PasswordsMatchException;
import com.online.gallery.exception.user.UserDuplicationException;
import com.online.gallery.exception.user.UserNotFoundException;
import com.online.gallery.mail.EmailMessageBuilder;
import com.online.gallery.mail.sender.DefaultMailSender;
import com.online.gallery.model.auth.ConfirmationToken;
import com.online.gallery.model.auth.PasswordResetToken;
import com.online.gallery.model.user.Role;
import com.online.gallery.model.user.User;
import com.online.gallery.repository.auth.ConfirmationTokenRepo;
import com.online.gallery.repository.auth.PasswordResetTokenRepo;
import com.online.gallery.repository.user.UserRepo;
import com.online.gallery.security.service.JwtService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Transactional
@Service
public class DefaultAuthService implements AuthService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final DefaultMailSender mailSender;
    private final AuthenticationManager authenticationManager;
    private final ConfirmationTokenRepo confirmationTokenRepo;
    private final PasswordResetTokenRepo passwordResetTokenRepo;
    private final String urlForEmailMessageGeneration = "http://localhost:8080";
    private final String endpointForCompleteSignUp = "/api/v1/auth/verify/";

    public DefaultAuthService(UserRepo userRepo,
                              PasswordEncoder passwordEncoder,
                              JwtService jwtService,
                              DefaultMailSender mailSender,
                              AuthenticationManager authenticationManager,
                              ConfirmationTokenRepo confirmationTokenRepo,
                              PasswordResetTokenRepo passwordResetTokenRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.mailSender = mailSender;
        this.authenticationManager = authenticationManager;
        this.confirmationTokenRepo = confirmationTokenRepo;
        this.passwordResetTokenRepo = passwordResetTokenRepo;
    }

    private AuthTokenResponse buildAuthResponse(User user) {
        var jwtToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        return new AuthTokenResponse(jwtToken, refreshToken);
    }

    public AuthTokenResponse processSignUp(SignUpRequest request) throws MessagingException {
        String userEmail = request.getEmail();
        userRepo.findByEmail(userEmail).ifPresent(user -> {
            if (user.isEnabled()) {
                throw new UserDuplicationException("user with this email already exist");
            } else {
                confirmationTokenRepo.findByUserId(user.getId())
                        .ifPresent(token -> {
                            if (!token.isExpired()) {
                                throw new TokenDuplicationException(
                                        "confirmation token has already been sent," +
                                                " please check your email");
                            } else {
                                confirmationTokenRepo.delete(token);
                                userRepo.delete(user);
                            }
                        });
            }
        });
        User user = User.builder()
                .id(new ObjectId().toString())
                .nickname(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(Role.USER)
                .build();
        userRepo.save(user);

        String token = new ObjectId().toString();

        confirmationTokenRepo.save(new ConfirmationToken(token, user.getId()));

        mailSender.sendConfirmationEmail(EmailMessageBuilder.BuildConfirmationEmail(
                        userEmail,
                        urlForEmailMessageGeneration + endpointForCompleteSignUp + token),
                userEmail,
                "Confirm Email");
        return buildAuthResponse(user);
    }

    public AuthTokenResponse signIn(SignInRequest request) {
        String userEmail = request.getEmail();
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("user not found"));

        user.checkIfUserEnabled();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userEmail, request.getPassword()));

        return buildAuthResponse(user);
    }

    public String completeSignUp(String token) {
        ConfirmationToken confirmationToken = confirmationTokenRepo.findById(token)
                .orElseThrow(() -> new TokenNotFoundException("confirmation token not found"));
        if (confirmationToken.isExpired()) {
            throw new TokenExpirationException("confirmation token is expired");
        }
        User user = userRepo.findById(confirmationToken.getUserId())
                .orElseThrow(() -> new UserNotFoundException("user not found"));
        user.setEnabled(true);
        userRepo.save(user);

        confirmationTokenRepo.deleteById(confirmationToken.getId());
        return "User successfully activated";
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new TokenInvalidException("authorization header is missing or not valid");
        }
        final String refreshToken = authHeader.substring(7);
        final String userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail == null) {
            throw new TokenInvalidException("failed to extract user from token");
        }

        var user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("user not found"));
        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new TokenInvalidException("refresh token is not valid");
        }

        var accessToken = jwtService.generateAccessToken(user);
        var authResponse = new AuthTokenResponse(accessToken, refreshToken);
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
    }

    public String sendMessageForResetPassword(String email) throws MessagingException {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user not found"));
        user.checkIfUserEnabled();
        Optional<PasswordResetToken> optionalPasswordResetToken =
                passwordResetTokenRepo.findByEmail(email);
        if (optionalPasswordResetToken.isPresent()) {
            PasswordResetToken passwordResetToken = optionalPasswordResetToken.get();
            if (passwordResetToken.getExpiredAt().isAfter(LocalDateTime.now())) {
                throw new TokenDuplicationException(
                        "password recovery link was already sent. Try again in 15 minutes");
            }
            passwordResetTokenRepo.deleteById(passwordResetToken.getId());
            sendMessageForResetPassword(email);
        }
        String token = new ObjectId().toString();
        passwordResetTokenRepo.save(new PasswordResetToken(token,
                user.getEmail()));
        mailSender.sendConfirmationEmail(
                EmailMessageBuilder.BuildResetEmail(
                        email, "http://localhost:8080/api/v1/auth/password/reset/" + token),
                email, "reset password");
        return "password recovery link has been successfully sent to this email";
    }

    public String processPasswordReset(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepo.findById(token)
                .orElseThrow(() -> new TokenNotFoundException("not found password reset token"));
        if (!userRepo.existsByEmail(passwordResetToken.getEmail())) {
            throw new UserNotFoundException("user not found");
        }
        if (passwordResetToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpirationException("password reset token is expired");
        }
        return "enter a new password";
    }

    public String completePasswordReset(String token, String newPassword) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepo.findById(token)
                .orElseThrow(() -> new TokenNotFoundException("not found password reset token"));
        User user = userRepo.findByEmail(passwordResetToken.getEmail())
                .orElseThrow(() -> new UserNotFoundException("user not found"));
        if (passwordResetToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpirationException("password reset token is expired");
        }
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new PasswordsMatchException("passwords match");
        }
        passwordResetTokenRepo.deleteById(token);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        return "password successfully changed";
    }
}