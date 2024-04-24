package com.online.gallery.service.auth;

import com.online.gallery.dto.request.SignInRequest;
import com.online.gallery.dto.request.SignUpRequest;
import com.online.gallery.dto.response.AuthTokenResponse;
import com.online.gallery.exception.user.UserDuplicationException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;

public interface AuthService {
    AuthTokenResponse processSignUp(SignUpRequest request) throws UserDuplicationException, MessagingException;

    AuthTokenResponse signIn(SignInRequest request) throws UsernameNotFoundException;

    String completeSignUp(String token);

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;

    String sendMessageForResetPassword(String token) throws MessagingException;

    String processPasswordReset(String token);

    String completePasswordReset(String token, String newPassword);

}
