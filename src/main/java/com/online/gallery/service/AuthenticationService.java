package com.online.gallery.service;

import com.online.gallery.dto.request.AuthenticationRequest;
import com.online.gallery.dto.request.RegisterRequest;
import com.online.gallery.dto.response.AuthenticationResponse;
import com.online.gallery.exception.UserDuplicationException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request) throws UserDuplicationException, MessagingException;

    AuthenticationResponse authenticate(AuthenticationRequest request) throws UsernameNotFoundException;

    String activate(String token);

    String sendMessageForReset(String token) throws MessagingException;

    String checkPasswordResetTokenAndUser(String token);

    String resetPassword(String token, String newPassword);

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
