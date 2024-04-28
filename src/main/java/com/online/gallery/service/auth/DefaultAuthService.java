package com.online.gallery.service.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.online.gallery.dto.request.SignInRequest;
import com.online.gallery.dto.request.SignUpRequest;
import com.online.gallery.dto.response.AuthTokenResponse;
import com.online.gallery.entity.auth.ConfirmationToken;
import com.online.gallery.entity.auth.PasswordResetToken;
import com.online.gallery.entity.user.Role;
import com.online.gallery.entity.user.User;
import com.online.gallery.exception.auth.AuthException;
import com.online.gallery.exception.auth.TokenDuplicationException;
import com.online.gallery.exception.auth.TokenExpirationException;
import com.online.gallery.exception.auth.TokenNotFoundException;
import com.online.gallery.exception.user.PasswordsMatchException;
import com.online.gallery.exception.user.UserDuplicationException;
import com.online.gallery.exception.user.UserNotEnabledException;
import com.online.gallery.exception.user.UserNotFoundException;
import com.online.gallery.mail.EmailMessageBuilder;
import com.online.gallery.mail.sender.DefaultMailSender;
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
    private final String userNotFoundMessage = "user not found";

    private final String urlForEmailMessageGeneration = "http://localhost:8080";

    private final String endpointForCompleteSignUp = "api/v1/auth/verify/";

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
                .email(userEmail)
                .role(Role.USER)
                .enabled(false)
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
        String email = request.getEmail();
        var user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(userNotFoundMessage));

        if (!user.isEnabled()) {
            throw new UserNotEnabledException("please confirm sign up with email");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword()));

        return buildAuthResponse(user);
    }

    public AuthTokenResponse completeSignUp(String token) {
        var confirmationToken = confirmationTokenRepo.findById(token)
                .orElseThrow(() -> new TokenNotFoundException("confirmation token not found"));
        if (confirmationToken.isExpired()) {
            throw new TokenExpirationException("confirmation token is expired");
        }

        confirmationTokenRepo.deleteById(confirmationToken.getId());

        var user = userRepo.findById(confirmationToken.getUserId())
                .orElseThrow(() -> new UserNotFoundException(userNotFoundMessage));
        user.setEnabled(true);
        return buildAuthResponse(userRepo.save(user));
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AuthException("authorization header is missing or not valid");
        }
        final String refreshToken = authHeader.substring(7);
        final String userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail == null) {
            throw new AuthException("failed to extract user from token");
        }

        var user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(userNotFoundMessage));
        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new AuthException("refresh token is not valid");
        }

        var accessToken = jwtService.generateAccessToken(user);
        var authResponse = new AuthTokenResponse(accessToken, refreshToken);
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
    }

    public String sendMessageForResetPassword(String email) throws MessagingException {
        var user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(userNotFoundMessage));
        if (!user.isEnabled()) {
            throw new UserNotEnabledException("please confirm sign up with email");
        }
        passwordResetTokenRepo.findByEmail(email).ifPresent(passwordResetToken -> {
            if (!passwordResetToken.isExpired()) {
                throw new TokenDuplicationException(
                        "password recovery link was already sent, try again in 15 minutes");
            }
            passwordResetTokenRepo.deleteById(passwordResetToken.getId());
        });
        String token = new ObjectId().toString();
        passwordResetTokenRepo.save(new PasswordResetToken(token, email));
        mailSender.sendConfirmationEmail(
                EmailMessageBuilder.BuildResetEmail(
                        email,
                        "http://localhost:8080api/v1/auth/password/" + token),
                email, "reset password");
        return "password recovery link has been successfully sent to this email";
    }

    public String processPasswordReset(String token) {
        var passwordResetToken = passwordResetTokenRepo.findById(token)
                .orElseThrow(() -> new TokenNotFoundException("password reset token not found"));
        if (!userRepo.existsByEmail(passwordResetToken.getEmail())) {
            throw new UserNotFoundException(userNotFoundMessage);
        }
        if (passwordResetToken.isExpired()) {
            throw new TokenExpirationException("password reset token is expired");
        }
        return "enter a new password";
    }

    public String completePasswordReset(String token, String newPassword) {
        var passwordResetToken = passwordResetTokenRepo.findById(token)
                .orElseThrow(() -> new TokenNotFoundException("password reset token not found"));
        var user = userRepo.findByEmail(passwordResetToken.getEmail())
                .orElseThrow(() -> new UserNotFoundException(userNotFoundMessage));
        if (passwordResetToken.isExpired()) {
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
