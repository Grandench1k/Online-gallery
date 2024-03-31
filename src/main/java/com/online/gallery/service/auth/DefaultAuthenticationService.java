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
import com.online.gallery.mail.sender.EmailMessageBuilder;
import com.online.gallery.mail.sender.DefaultMailSender;
import com.online.gallery.model.auth.ConfirmationToken;
import com.online.gallery.model.auth.PasswordResetToken;
import com.online.gallery.model.user.Role;
import com.online.gallery.model.user.User;
import com.online.gallery.repository.ConfirmationTokenRepository;
import com.online.gallery.repository.PasswordResetTokenRepository;
import com.online.gallery.repository.UserRepository;
import com.online.gallery.security.service.JwtService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DefaultAuthenticationService implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final DefaultMailSender defaultMailSender;
    private final AuthenticationManager authenticationManager;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public DefaultAuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, DefaultMailSender defaultMailSender, AuthenticationManager authenticationManager, ConfirmationTokenRepository confirmationTokenRepository, PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.defaultMailSender = defaultMailSender;
        this.authenticationManager = authenticationManager;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    public AuthTokenResponse register(SignUpRequest request) throws UserDuplicationException, MessagingException {
        String email = request.getEmail();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User userFromDatabase = optionalUser.get();
            if (userFromDatabase.isEnabled()) {
                throw new UserDuplicationException("this user already exist.");
            }
            if (userFromDatabase.isAccountNonExpired()) {
                throw new UserDuplicationException("this user already exist, but not registered with email. Try again in 15 minutes.");
            }
            userRepository.delete(userFromDatabase);
            return register(request);
        }
        User user = User.builder()
                .id(new ObjectId().toString())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(email)
                .role(Role.USER)
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .build();
        String token = new ObjectId().toString();
        confirmationTokenRepository.save(new ConfirmationToken(token,
                user.getId()));
        defaultMailSender.sendConfirmationEmail(
                EmailMessageBuilder.BuildConfirmationEmail(
                        email, "http://localhost:8080/api/v1/auth/activate/" + token), email, "confirm email");
        userRepository.save(user);
        var jwtToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        return AuthTokenResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).build();
    }

    public AuthTokenResponse authenticate(SignInRequest request) throws UsernameNotFoundException {
        String email = request.getEmail();
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user not found."));
        user.checkIfUserEnabled();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword()));
        var jwtToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        return AuthTokenResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).build();
    }

    public String activate(String token) {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findById(token)
                .orElseThrow(() -> new TokenNotFoundException("confirmation token not found."));
        confirmationTokenRepository.deleteById(confirmationToken.getId());
        User unverifiedUser = userRepository.findById(confirmationToken.getUserId())
                .orElseThrow(() -> new UserNotFoundException("user not found."));
        if (confirmationToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpirationException("confirmation token is expired.");
        }
        unverifiedUser.setEnabled(true);
        userRepository.save(unverifiedUser);
        return token;
    }

    public String sendMessageForReset(String email) throws MessagingException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user not found."));
        user.checkIfUserEnabled();
        Optional<PasswordResetToken> optionalPasswordResetToken = passwordResetTokenRepository.findByEmail(email);
        if (optionalPasswordResetToken.isPresent()) {
            PasswordResetToken passwordResetToken = optionalPasswordResetToken.get();
            if (passwordResetToken.getExpiredAt().isAfter(LocalDateTime.now())) {
                throw new TokenDuplicationException("the password recovery link was sent. Try again in 15 minutes.");
            }
            passwordResetTokenRepository.deleteById(passwordResetToken.getId());
            sendMessageForReset(email);
        }
        String token = new ObjectId().toString();
        passwordResetTokenRepository.save(new PasswordResetToken(token,
                user.getEmail()));
        defaultMailSender.sendConfirmationEmail(
                EmailMessageBuilder.BuildResetEmail(
                        email, "http://localhost:8080/api/v1/auth/password/reset/" + token), email, "reset password.");
        return "the password recovery link has been successfully sent to this email.";
    }

    public String checkPasswordResetTokenAndUser(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findById(token)
                .orElseThrow(() -> new TokenNotFoundException("password reset token not found."));
        if (!userRepository.existsByEmail(passwordResetToken.getEmail())) {
            throw new UserNotFoundException("user not found.");
        }
        if (passwordResetToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpirationException("password reset token is expired.");
        }
        return "enter a new password.";
    }

    public String resetPassword(String token, String newPassword) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findById(token)
                .orElseThrow(() -> new TokenNotFoundException("password reset token not found."));
        User user = userRepository.findByEmail(passwordResetToken.getEmail())
                .orElseThrow(() -> new UserNotFoundException("user not found."));
        if (passwordResetToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpirationException("password reset token is expired.");
        }
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new PasswordsMatchException("passwords match.");
        }
        passwordResetTokenRepository.deleteById(token);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return "password successfully changed.";
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
                    .orElseThrow(() -> new UserNotFoundException("user not found."));
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateAccessToken(user);
                var authResponse = AuthTokenResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
