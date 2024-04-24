package com.online.gallery.controller.media.video;

import com.online.gallery.model.media.Video;
import com.online.gallery.service.media.video.VideoService;
import com.online.gallery.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public ResponseEntity<List<Video>> listAllVideos(Authentication authentication) {
        return ResponseEntity
                .ok()
                .body(videoService.findAllVideos(userService.getUserId(authentication)));
    }

    @GetMapping(value = "/{videoId}", produces = {"video/mp4", "video/mpeg", "video/ogg"})
    public ResponseEntity<byte[]> getVideoById(
            @PathVariable String videoId,
            Authentication authentication) {
        return ResponseEntity
                .ok()
                .contentType(MediaType.valueOf("video/mp4"))
                .body(videoService.findVideoById(videoId, userService.getUserId(authentication)));
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Video> saveVideo(
            @RequestPart @Valid Video video,
            @RequestPart MultipartFile videoFile,
            Authentication authentication) throws IOException {
        return ResponseEntity
                .ok()
                .body(videoService.saveVideo(video, videoFile, userService.getUserId(authentication)));
    }

    @PatchMapping("/{videoId}")
    public ResponseEntity<Video> updateVideoById(
            @PathVariable String videoId,
            @RequestBody @Valid Video video,
            Authentication authentication) {
        return ResponseEntity
                .ok()
                .body(videoService.updateVideoById(videoId, video, userService.getUserId(authentication)));
    }

    @DeleteMapping("/{videoId}")
    public ResponseEntity<Video> deleteVideoById(@PathVariable String videoId, Authentication authentication) {
        return ResponseEntity
                .ok()
                .body(videoService.deleteVideoById(videoId, userService.getUserId(authentication)));
    }
}