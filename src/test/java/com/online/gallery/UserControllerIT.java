package com.online.gallery;

import com.online.gallery.dto.request.ImageFileUploadRequest;
import com.online.gallery.dto.response.PresignedLinkResponse;
import com.online.gallery.entity.media.Image;
import com.online.gallery.entity.user.Role;
import com.online.gallery.entity.user.User;
import com.online.gallery.security.service.JwtService;
import com.online.gallery.service.user.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ITGalleryAnnotation
@RequiredArgsConstructor
public class UserControllerIT {

    private final MockMvc mockMvc;

    private final String userId = "userId";
    private final String message = "message";
    private final String errorMessage = "error";
    private final PresignedLinkResponse presignedLinkResponse = new PresignedLinkResponse(10, message);
    private final String baseLink = "/api/v1/users";
    private final String profileLink = baseLink + "/profileImage";
    private final MediaType applicationJson = MediaType.APPLICATION_JSON;
    private final User user = new User(userId,
            "nickname",
            "email@mail.com",
            "password",
            "imageId",
            Role.USER,
            true);
    private final ResultMatcher okStatus = status().isOk();

    @MockBean
    private SecurityFilterChain securityFilterChain;
    @MockBean
    private UserService userService;
    @MockBean
    private JwtService jwtService;

    @PostConstruct
    void setUp() {
        when(userService.getUserId(any()))
                .thenReturn(userId);
        when(userService.getUser(any()))
                .thenReturn(user);
    }

    private ResultMatcher matchValue(String expression, String value) {
        return jsonPath(expression).value(value);
    }

    @Test
    public void getProfileImage() throws Exception {
        //given
        var requestBuilder = get(profileLink + "/generate-get-url");
        //when 
        when(userService.generatePresignedGetUrl(any(User.class))).thenReturn(presignedLinkResponse);

        mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(okStatus,
                        matchValue("$.link", message));
    }

    @Test
    public void getUser() throws Exception {
        //given
        var requestBuilder = get(baseLink);
        //when 
        when(userService.getUser(any(User.class))).thenReturn(user);

        mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(okStatus,
                        content().contentType(applicationJson),
                        matchValue("$.data.id", user.getId()),
                        matchValue("$.data.username", user.getUsername()),
                        matchValue("$.data.email", user.getEmail()),
                        matchValue("$.data.role", "USER"),
                        matchValue("$.data.password", user.getPassword()),
                        matchValue("$.data.profile_image_name", user.getProfileImageName())
                );
    }

    @Test
    public void prepareUserProfileImageUpload() throws Exception {
        // given
        String body =
                """
                        {
                            "id" : "507f1f77bcf86cd799439011",
                            "name": "newProfileImage.png",
                            "contentType": "image/png",
                            "size": "102400"
                        }""";
        var requestBuilder = post(profileLink + "/generate-upload-url")
                .content(body)
                .contentType(applicationJson);
        //when
        when(userService.generatePresignedPutUrl(any(ImageFileUploadRequest.class), any(User.class)))
                .thenReturn(new PresignedLinkResponse(300, baseLink));

        mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(okStatus,
                        content().contentType(applicationJson),
                        matchValue("$.link", baseLink));
    }

    @Test
    public void saveProfileImage() throws Exception {
        // given
        String body =
                """
                        {
                            "id" : "507f1f77bcf86cd799439011",
                            "name": "new UserImage"
                        }""";
        var requestBuilder = post(profileLink)
                .content(body)
                .contentType(applicationJson);
        //when 
        when(userService.saveProfileImage(any(Image.class), any(User.class)))
                .thenReturn(user);

        mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(okStatus,
                        content().contentType(applicationJson),
                        matchValue("$.data.id", user.getId()),
                        matchValue("$.data.username", user.getUsername()),
                        matchValue("$.data.email", user.getEmail()),
                        matchValue("$.data.role", "USER"),
                        matchValue("$.data.password", user.getPassword()),
                        matchValue("$.data.profile_image_name", user.getProfileImageName()));
    }

    @Test
    public void updatePassword() throws Exception {
        // given
        String passwords =
                """
                        {
                            "old_password": "example",
                            "new_password": "example1"
                        }""";
        String success = "success";
        var requestBuilder = put(baseLink + "/password")
                .content(passwords)
                .contentType(applicationJson);
        //when 
        when(userService.updateUserPassword(anyString(), anyString(), any()))
                .thenReturn(success);

        mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(okStatus,
                        content().contentType(applicationJson),
                        matchValue("$.message", success));
    }
}

