package com.online.gallery;

import com.online.gallery.dto.request.VideoFileUploadRequest;
import com.online.gallery.dto.response.PresignedLinkResponse;
import com.online.gallery.entity.media.Video;
import com.online.gallery.security.service.JwtService;
import com.online.gallery.service.media.video.VideoService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ITGalleryAnnotation
@RequiredArgsConstructor
public class VideoControllerIT {

    private final MockMvc mockMvc;

    private final String id = "id";
    private final String message = "message";
    private final String errorMessage = "error";
    private final String baseLink = "/api/v1/videos";
    private final PresignedLinkResponse presignedLinkResponse = new PresignedLinkResponse(10, message);
    private final String name = "name";
    private final String path = "url";
    private final String userId = "user_id";
    private final MediaType applicationJson = MediaType.APPLICATION_JSON;

    @MockBean
    private SecurityFilterChain securityFilterChain;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private VideoService videoService;
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
    public void listAllUserVideos() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders
                .get(baseLink);
        List<Video> videoList = List.of(new Video(id, name, path, userId));
        //When
        when(videoService.findAllVideos(anyString()))
                .thenReturn(videoList);
        mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(applicationJson),
                        matchValue("$.data[0].id", id),
                        matchValue("$.data[0].name", name),
                        matchValue("$.data[0].file_path", path),
                        matchValue("$.data[0].user_id", userId)
                );
    }

    @Test
    public void getUserVideoById() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders
                .get(baseLink + "/" + id);
        //When
        when(videoService.findVideoById(anyString(), anyString()))
                .thenReturn(new Video(id, name, path, userId));

        mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(applicationJson),
                        matchValue("$.data.id", id),
                        matchValue("$.data.name", name),
                        matchValue("$.data.file_path", path),
                        matchValue("$.data.user_id", userId)
                );
    }

    @Test
    public void generatePresignedGetVideoUrl() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders
                .get(baseLink + "/generate-get-url/" + id)
                .contentType(applicationJson);
        //When
        when(videoService.generatePresignedGetVideoUrl(anyString(), anyString()))
                .thenReturn(presignedLinkResponse);

        mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(applicationJson),
                        matchValue("$.link", message)
                );
    }

    @Test
    public void prepareVideoUpload() throws Exception {
        //given
        String body =
                """
                        {
                            "id" : "507f1f77bcf86cd799439011",
                            "name": "new Video",
                            "contentType": "video/mp4",
                            "size": "200000"
                        }""";
        var requestBuilder = MockMvcRequestBuilders
                .post(baseLink + "/generate-upload-url")
                .content(body)
                .contentType(applicationJson);
        //When
        when(videoService.generatePresignedPutUrl(any(VideoFileUploadRequest.class), anyString()))
                .thenReturn(presignedLinkResponse);

        mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(applicationJson),
                        matchValue("$.link", message)
                );
    }

    @Test
    public void saveVideo() throws Exception {
        //given
        String body =
                """
                        {
                            "id" : "507f1f77bcf86cd799439011",
                            "name": "name"
                        }""";
        //When
        when(videoService.saveVideo(any(Video.class), anyString()))
                .thenReturn(new Video(id, name, path, userId));

        mockMvc.perform(MockMvcRequestBuilders
                        .post(baseLink)
                        .content(body)
                        .contentType(applicationJson))
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(applicationJson),
                        matchValue("$.data.id", id),
                        matchValue("$.data.name", name),
                        matchValue("$.data.file_path", path),
                        matchValue("$.data.user_id", userId)
                );
    }

    @Test
    public void updateVideoById() throws Exception {
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
        //When
        when(videoService.updateVideoById(any(Video.class), anyString(), anyString()))
                .thenReturn(new Video(id, name, path, userId));

        mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(applicationJson),
                        matchValue("$.data.id", id),
                        matchValue("$.data.name", name),
                        matchValue("$.data.file_path", path),
                        matchValue("$.data.user_id", userId)
                );
    }

    @Test
    public void deleteUserVideoById() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders
                .delete(baseLink + "/" + id);
        //When
        when(videoService.deleteVideoById(anyString(), anyString()))
                .thenReturn(new Video(id, name, path, userId));
        mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(applicationJson),
                        matchValue("$.data.id", id),
                        matchValue("$.data.name", name),
                        matchValue("$.data.file_path", path),
                        matchValue("$.data.user_id", userId)
                );
    }
}