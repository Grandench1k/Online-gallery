package com.online.gallery;


import com.online.gallery.dto.request.ImageDetailsRequest;
import com.online.gallery.dto.request.ImageFileUploadRequest;
import com.online.gallery.dto.response.PresignedLinkResponse;
import com.online.gallery.entity.media.Image;
import com.online.gallery.security.service.JwtService;
import com.online.gallery.service.media.image.ImageService;
import com.online.gallery.service.user.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ITGalleryAnnotation
@RequiredArgsConstructor
public class ImageControllerIT {

    private final MockMvc mockMvc;

    private final String id = "id";
    private final String message = "message";
    private final String errorMessage = "error";
    private final PresignedLinkResponse presignedLinkResponse = new PresignedLinkResponse(10, message);
    private final String userId = "user_id";
    private final String name = "name";
    private final String path = "path";
    private final String baseLink = "/api/v1/images";
    private final MediaType applicationJson = MediaType.APPLICATION_JSON;

    @MockBean
    private SecurityFilterChain securityFilterChain;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private ImageService imageService;
    @MockBean
    private UserService userService;

    @PostConstruct
    void setUp() {
        when(userService.getUserId(any())).thenReturn(userId);
    }


    private ResultMatcher matchValue(String expression, String value) {
        return jsonPath(expression).value(value);
    }

    @Test
    public void listAllImages() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders.get(baseLink);
        List<Image> imageList = List.of(new Image(id, name, path, userId));
        //when
        when(imageService.findAllImages(anyString())).thenReturn(imageList);

        mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(applicationJson),
                        matchValue("$.data[0].id", id),
                        matchValue("$.data[0].name", name),
                        matchValue("$.data[0].file_path", path),
                        matchValue("$.data[0].user_id", userId));
    }

    @Test
    public void getImageById() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders
                .get(baseLink + "/" + id)
                .contentType(applicationJson);
        //when
        when(imageService.findImageById(anyString(), anyString()))
                .thenReturn(new Image(id, name, path, userId));

        mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(applicationJson),
                        matchValue("$.data.id", id),
                        matchValue("$.data.name", name),
                        matchValue("$.data.file_path", path),
                        matchValue("$.data.user_id", userId));
    }

    @Test
    public void generatePresignedGetImageUrl() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders
                .get(baseLink + "/generate-get-url/" + id)
                .contentType(applicationJson);
        //when
        when(imageService.generatePresignedGetImageUrl(anyString(), anyString()))
                .thenReturn(presignedLinkResponse);

        mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(applicationJson),
                        matchValue("$.link", message));
    }

    @Test
    public void prepareImageUpload() throws Exception {
        //given
        String body =
                """
                        {
                            "id" : "507f1f77bcf86cd799439011",
                            "name": "new Image",
                            "contentType": "image/png",
                            "size": "100000"
                        }""";
        var requestBuilder = post(baseLink + "/generate-upload-url")
                .content(body)
                .contentType(applicationJson);
        //when
        when(imageService.generatePresignedPutUrl(any(ImageFileUploadRequest.class), anyString()))
                .thenReturn(presignedLinkResponse);

        mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(applicationJson),
                        matchValue("$.link", message));
    }

    @Test
    public void saveImage() throws Exception {
        //given
        String body =
                """
                        {
                            "id" : "507f1f77bcf86cd799439011",
                            "name": "name"
                        }""";
        var requestBuilder = post(baseLink)
                .content(body)
                .contentType(applicationJson);
        //when
        when(imageService.saveImage(any(Image.class), anyString()))
                .thenReturn(new Image(id, name, path, userId));

        mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(applicationJson),
                        matchValue("$.data.id", id),
                        matchValue("$.data.name", name),
                        matchValue("$.data.file_path", path),
                        matchValue("$.data.user_id", userId));
    }

    @Test
    public void updateImage() throws Exception {
        //given
        String body =
                """
                        {
                        "name": "name"
                        }""";
        var requestBuilder = MockMvcRequestBuilders
                .put(baseLink + "/" + id)
                .content(body)
                .contentType(applicationJson);
        //when
        when(imageService.updateImageById(any(Image.class), anyString(), anyString()))
                .thenReturn(new Image(id, name, path, userId));

        mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(applicationJson),
                        matchValue("$.data.id", id),
                        matchValue("$.data.name", name),
                        matchValue("$.data.file_path", path),
                        matchValue("$.data.user_id", userId));
    }

    @Test
    public void updateImageDetails() throws Exception {
        //given
        String body =
                """
                        {
                        "name": "name"
                        }""";
        var requestBuilder = MockMvcRequestBuilders
                .patch(baseLink + "/" + id)
                .content(body)
                .contentType(applicationJson);
        //when
        when(imageService.updateImageDetailsById(any(ImageDetailsRequest.class), anyString(), anyString()))
                .thenReturn(new Image(id, name, path, userId));

        mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(applicationJson),
                        matchValue("$.data.id", id),
                        matchValue("$.data.name", name),
                        matchValue("$.data.file_path", path),
                        matchValue("$.data.user_id", userId));
    }

    @Test
    public void deleteImageById() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders
                .delete(baseLink + "/" + id);
        //when
        when(imageService.deleteImageById(anyString(), anyString()))
                .thenReturn(new Image(id, name, path, userId));

        mockMvc.perform(
                        requestBuilder)
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(applicationJson),
                        matchValue("$.data.id", id),
                        matchValue("$.data.name", name),
                        matchValue("$.data.file_path", path),
                        matchValue("$.data.user_id", userId));
    }
}