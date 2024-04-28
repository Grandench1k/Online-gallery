package com.online.gallery;


import com.online.gallery.dto.request.SignInRequest;
import com.online.gallery.dto.request.SignUpRequest;
import com.online.gallery.dto.response.AuthTokenResponse;
import com.online.gallery.security.service.JwtService;
import com.online.gallery.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ITGalleryAnnotation
@RequiredArgsConstructor
public class AuthControllerIT {

    private final MockMvc mockMvc;

    private final String accessToken = "token";
    private final String refreshToken = "refreshToken";
    private final String successful = "successful";
    private final String baseLink = "/api/v1/auth";
    private final String errorMessage = "error";
    private final MediaType applicationJson = MediaType.APPLICATION_JSON;

    @MockBean
    private JwtService jwtService;
    @MockBean
    private SecurityFilterChain securityFilterChain;
    @MockBean
    private AuthService authService;

    private ResultMatcher matchValue(String expression, String value) {
        return jsonPath(expression).value(value);
    }

    @Test
    void signUp() throws Exception {
        //given
        String body = """
                {
                    "username": "firstname1",
                    "email": "example2@mail.org",
                    "password": "password123"
                }""";
        var requestBuilder = post(baseLink + "/signup")
                .content(body)
                .contentType(applicationJson);
        //when
        when(authService.processSignUp(any(SignUpRequest.class)))
                .thenReturn(new AuthTokenResponse(accessToken, refreshToken));

        mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(applicationJson),
                        matchValue("$.access_token", accessToken),
                        matchValue("$.refresh_token", refreshToken));
    }

    @Test
    void confirmSignUp() throws Exception {
        //given
        var requestBuilder = get(baseLink + "/verify/id");
        //when
        when(authService.completeSignUp(anyString()))
                .thenReturn(new AuthTokenResponse(accessToken, refreshToken));

        signUp();

        mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(applicationJson),
                        matchValue("$.access_token", accessToken),
                        matchValue("$.refresh_token", refreshToken));
    }

    @Test
    void signIn() throws Exception {
        //given
        String body = """
                {
                    "username": "firstname",
                    "email": "exapmle@mail.org",
                    "password": "example"
                }""";
        var requestBuilder = post(baseLink + "/signin")
                .content(body)
                .contentType(applicationJson);
        //when
        when(authService.signIn(any(SignInRequest.class)))
                .thenReturn(new AuthTokenResponse(accessToken, refreshToken));
        mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(applicationJson),
                        matchValue("$.access_token", accessToken),
                        matchValue("$.refresh_token", refreshToken));
    }

    @Test
    void logOut() throws Exception {
        //when
        mockMvc.perform(post(baseLink + "/logout"))
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(applicationJson),
                        matchValue("$.message", "successful logout"));
    }

    @Test
    void refreshToken() throws Exception {
        //given
        var requestBuilder = post(baseLink + "/refresh");
        //when
        doNothing()
                .when(authService).refreshToken(any(HttpServletRequest.class),
                        any(HttpServletResponse.class));
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    void forgotPassword() throws Exception {
        //given
        String body = """
                {
                    "email": "exapmle@mail.org"
                }""";
        var requestBuilder = post(baseLink + "/password")
                .content(body)
                .contentType(applicationJson);
        //when
        when(authService.sendMessageForResetPassword(anyString()))
                .thenReturn(successful);
        mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(applicationJson),
                        matchValue("$.message", successful));
    }

    @Test
    void resetPassword() throws Exception {
        //given
        String body = """
                {
                    "password": "example123"
                }""";
        var requestBuilder = post(baseLink + "/password/" + "id")
                .content(body)
                .contentType(applicationJson);
        //when
        when(authService.completePasswordReset(anyString(), anyString()))
                .thenReturn(successful);

        forgotPassword();
        mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(applicationJson),
                        matchValue("$.message", successful));
    }
}
