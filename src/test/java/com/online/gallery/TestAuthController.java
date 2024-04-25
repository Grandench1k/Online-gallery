package com.online.gallery;


import com.online.gallery.model.auth.ConfirmationToken;
import com.online.gallery.model.auth.PasswordResetToken;
import com.online.gallery.model.user.Role;
import com.online.gallery.model.user.User;
import com.online.gallery.repository.auth.ConfirmationTokenRepo;
import com.online.gallery.repository.auth.PasswordResetTokenRepo;
import com.online.gallery.repository.user.UserRepo;
import com.online.gallery.security.configuration.ApplicationConfiguration;
import com.online.gallery.security.service.JwtService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TestAuthController {
    private final String userId = new ObjectId().toString();
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ApplicationConfiguration applicationConfiguration;
    @Autowired
    private ConfirmationTokenRepo confirmationTokenRepo;
    private String jwtToken;
    private String refreshToken;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordResetTokenRepo passwordResetTokenRepo;

    @BeforeEach
    void setUp() {
        String encodedPassword = applicationConfiguration.passwordEncoder().encode("example");
        User user = User.builder().nickname("firstname").email("exapmle@mail.org").role(Role.USER).id(userId).password(encodedPassword).build();
        ConfirmationToken confirmationToken = new ConfirmationToken("id", userId);
        confirmationTokenRepo.save(confirmationToken);
        user.setEnabled(true);
        userRepo.save(user);
        jwtToken = jwtService.generateAccessToken(user);
        jwtToken = jwtService.generateRefreshToken(user);
    }

    @AfterEach
    void tearDown() {
        Optional<ConfirmationToken> confirmationToken = confirmationTokenRepo.findByUserId(userId);
        confirmationToken.ifPresent(value -> confirmationTokenRepo.delete(value));
        Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepo.findByEmail("exapmle@mail.org");
        passwordResetToken.ifPresent(value -> passwordResetTokenRepo.delete(value));
        Optional<User> user = userRepo.findByEmail("exapmle@mail.org");
        user.ifPresent(value -> userRepo.delete(value));
        Optional<User> user2 = userRepo.findByEmail("exapmle2@mail.org");
        user2.ifPresent(value -> userRepo.delete(value));
    }

    @Test
    void signUp() throws Exception {
        String body = """
                {
                    "username": "firstname1",
                    "email": "exapmle2@mail.org",
                    "password": "password123"
                }""";
        //When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signup").content(body).contentType("application/json"));
        //Then
        response.andExpect(status().isOk());
    }

    @Test
    void confirmSignUp() throws Exception {
        String body = """
                {
                    "username": "firstname",
                    "email": "exapmle2@mail.org",
                    "password": "password123"
                }""";
        //When
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signup").content(body).contentType("application/json"));
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/signup/" + "id"));

        //Then
        response.andExpect(status().isOk());
    }

    @Test
    void signIn() throws Exception {
        String body = """
                {
                    "username": "firstname",
                    "email": "exapmle@mail.org",
                    "password": "example"
                }""";
        //When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signin").content(body).contentType("application/json"));
        //Then
        response.andExpect(status().isOk());
    }

    @Test
    void refreshToken() throws Exception {
        //When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/refresh").header("Authorization", "Bearer " + jwtToken));
        //Then
        response.andExpect(status().isOk());
    }

    @Test
    void forgotPassword() throws Exception {
        String body = """
                {
                    "email": "exapmle@mail.org"
                }""";
        //When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/password").content(body).contentType(MediaType.APPLICATION_JSON));
        //Then
        response.andExpect(status().isOk());
    }

    @Test
    void resetPassword() throws Exception {
        String id = "id";
        PasswordResetToken passwordResetToken =
                new PasswordResetToken(id, "exapmle@mail.org");
        passwordResetTokenRepo.save(passwordResetToken);
        String body = """
                {
                    "password": "example123"
                }""";
        //When
        ResultActions firstResponse =
                mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/password/" + passwordResetToken.getId()));
        ResultActions secondResponse =
                mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/password/" + passwordResetToken.getId())
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON));
        //Then
        firstResponse.andExpect(status().isOk());
        secondResponse.andExpect(status().isOk());
    }
}
