package com.online.gallery.service.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.online.gallery.dto.request.SignInRequest;
import com.online.gallery.dto.request.SignUpRequest;
import com.online.gallery.dto.response.AuthTokenResponse;
import com.online.gallery.exception.auth.TokenDuplicationException;
import com.online.gallery.exception.auth.TokenExpirationException;
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
import com.online.gallery.repository.auth.ConfirmationTokenRepository;
import com.online.gallery.repository.auth.PasswordResetTokenRepository;
import com.online.gallery.repository.user.UserRepository;
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
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final DefaultMailSender mailSender;
    private final AuthenticationManager authenticationManager;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final String urlForEmailMessageGeneration = "http://localhost:8080";
    private String endpointForCompleteSignUp = "/api/v1/auth/verify/";

    public DefaultAuthService(UserRepository userRepository,
                              PasswordEncoder passwordEncoder,
                              JwtService jwtService,
                              DefaultMailSender mailSender,
                              AuthenticationManager authenticationManager,
                              ConfirmationTokenRepository confirmationTokenRepository,
                              PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.mailSender = mailSender;
        this.authenticationManager = authenticationManager;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    private AuthTokenResponse buildAuthResponse(User user) {
        var jwtToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        return new AuthTokenResponse(jwtToken, refreshToken);
    }

    public AuthTokenResponse processSignUp(SignUpRequest request) throws MessagingException {
        String userEmail = request.getEmail();
        userRepository.findByEmail(userEmail).ifPresent(user -> {
            if (user.isEnabled()) {
                throw new UserDuplicationException("user with this email already exist");
            } else {
                confirmationTokenRepository.findByUserId(user.getId())
                        .ifPresent(token -> {
                            if (!token.isExpired()) {
                                throw new TokenDuplicationException(
                                        "confirmation token has already been sent," +
                                                " please check your email");
                            } else {
                                confirmationTokenRepository.delete(token);
                                userRepository.delete(user);
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
        userRepository.save(user);

        String token = new ObjectId().toString();

        confirmationTokenRepository.save(new ConfirmationToken(token, user.getId()));

        mailSender.sendConfirmationEmail(EmailMessageBuilder.BuildConfirmationEmail(
                        userEmail,
                        urlForEmailMessageGeneration + endpointForCompleteSignUp + token),
                userEmail,
                "Confirm Email");
        return buildAuthResponse(user);
    }

    public AuthTokenResponse signIn(SignInRequest request) {
        String userEmail = request.getEmail();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        user.checkIfUserEnabled();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userEmail, request.getPassword()));

        return buildAuthResponse(user);
    }

    public String completeSignUp(String token) {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findById(token)
                .orElseThrow(() -> new TokenNotFoundException("Confirmation token not found."));
        if (confirmationToken.isExpired()) {
            throw new TokenExpirationException("confirmation token is expired");
        }
        User user = userRepository.findById(confirmationToken.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found."));
        user.setEnabled(true);
        userRepository.save(user);

        confirmationTokenRepository.deleteById(confirmationToken.getId());
        return "User successfully activated.";
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new UserNotFoundException("user not found"));
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateAccessToken(user);
                var authResponse = new AuthTokenResponse(accessToken, refreshToken);
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    public String sendMessageForResetPassword(String email) throws MessagingException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user not found"));
        user.checkIfUserEnabled();
        Optional<PasswordResetToken> optionalPasswordResetToken =
                passwordResetTokenRepository.findByEmail(email);
        if (optionalPasswordResetToken.isPresent()) {
            PasswordResetToken passwordResetToken = optionalPasswordResetToken.get();
            if (passwordResetToken.getExpiredAt().isAfter(LocalDateTime.now())) {
                throw new TokenDuplicationException(
                        "password recovery link was already sent. Try again in 15 minutes");
            }
            passwordResetTokenRepository.deleteById(passwordResetToken.getId());
            sendMessageForResetPassword(email);
        }
        String token = new ObjectId().toString();
        passwordResetTokenRepository.save(new PasswordResetToken(token,
                user.getEmail()));
        mailSender.sendConfirmationEmail(
                EmailMessageBuilder.BuildResetEmail(
                        email, "http://localhost:8080/api/v1/auth/password/reset/" + token),
                email, "reset password");
        return "password recovery link has been successfully sent to this email";
    }

    public String processPasswordReset(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findById(token)
                .orElseThrow(() -> new TokenNotFoundException("not found password reset token"));
        if (!userRepository.existsByEmail(passwordResetToken.getEmail())) {
            throw new UserNotFoundException("user not found");
        }
        if (passwordResetToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpirationException("password reset token is expired");
        }
        return "enter a new password";
    }

    public String completePasswordReset(String token, String newPassword) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findById(token)
                .orElseThrow(() -> new TokenNotFoundException("not found password reset token"));
        User user = userRepository.findByEmail(passwordResetToken.getEmail())
                .orElseThrow(() -> new UserNotFoundException("user not found"));
        if (passwordResetToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpirationException("password reset token is expired");
        }
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new PasswordsMatchException("passwords match");
        }
        passwordResetTokenRepository.deleteById(token);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return "password successfully changed";
    }
}