package com.online.gallery.controller.media.video;

import com.online.gallery.dto.request.VideoDetailsRequest;
import com.online.gallery.dto.request.VideoFileUploadRequest;
import com.online.gallery.dto.response.DataResponse;
import com.online.gallery.dto.response.PresignedLinkResponse;
import com.online.gallery.entity.media.Video;
import com.online.gallery.entity.user.User;
import com.online.gallery.service.media.video.VideoService;
import com.online.gallery.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/videos")
public class DefaultVideoController implements VideoController {
    private final VideoService videoService;
    private final UserService userService;

    public DefaultVideoController(VideoService videoService, UserService userService) {
        this.videoService = videoService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<DataResponse<List<Video>>> listAllVideos(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(new DataResponse<>(
                videoService.findAllVideos(userService.getUserId(user))));
    }

    @GetMapping("/{videoId}")
    public ResponseEntity<DataResponse<Video>> getVideoById(
            @PathVariable String videoId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(new DataResponse<>(
                videoService.findVideoById(videoId, userService.getUserId(user))));
    }

    @GetMapping("/generate-get-url/{videoId}")
    public ResponseEntity<PresignedLinkResponse> generatePresignedGetVideoUrl(
            @PathVariable String videoId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(videoService.generatePresignedGetVideoUrl(
                videoId, userService.getUserId(user)));
    }

    @PostMapping("/generate-upload-url")
    public ResponseEntity<PresignedLinkResponse> prepareVideoUpload(
            @RequestBody @Valid VideoFileUploadRequest videoFileUploadRequest,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(videoService.generatePresignedPutUrl(
                videoFileUploadRequest, userService.getUserId(user)));
    }

    @PostMapping()
    public ResponseEntity<DataResponse<Video>> saveVideo(
            @RequestBody @Valid Video video,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(new DataResponse<>(
                videoService.saveVideo(video, userService.getUserId(user))));
    }

    @PutMapping("/{videoId}")
    public ResponseEntity<DataResponse<Video>> updateVideoById(
            @RequestBody @Valid Video video,
            @PathVariable String videoId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(new DataResponse<>(
                videoService.updateVideoById(video, videoId, userService.getUserId(user))));
    }

    @PatchMapping("/{videoId}")
    public ResponseEntity<DataResponse<Video>> updateVideoDetailsById(
            @RequestBody @Valid VideoDetailsRequest videoDetailsRequest,
            @PathVariable String videoId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(new DataResponse<>(
                videoService.updateVideoDetailsById(videoDetailsRequest, videoId, userService.getUserId(user))));
    }

    @DeleteMapping("/{videoId}")
    public ResponseEntity<DataResponse<Video>> deleteVideoById(
            @PathVariable String videoId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(new DataResponse<>(
                videoService.deleteVideoById(videoId, userService.getUserId(user))));
    }
}
