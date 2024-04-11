package com.online.gallery.controller.media.video;

import com.online.gallery.model.media.Video;
import com.online.gallery.service.media.video.DefaultVideoService;
import com.online.gallery.service.user.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/videos")
public class DefaultVideoController implements VideoController {
    private final DefaultVideoService videoServiceImpl;
    private final UserService userService;

    public DefaultVideoController(DefaultVideoService videoServiceImpl, UserService userService) {
        this.videoServiceImpl = videoServiceImpl;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Video>> getAllVideos(Authentication authentication) {
        return ResponseEntity
                .ok()
                .body(videoServiceImpl.findAllVideos(userService.getUserId(authentication)));
    }

    @GetMapping(value = "/{videoId}", produces = {"video/mp4", "video/mpeg", "video/ogg"})
    public ResponseEntity<byte[]> getVideoById(
            @PathVariable String videoId,
            Authentication authentication) {
        return ResponseEntity
                .ok()
                .contentType(MediaType.valueOf("video/mp4"))
                .body(videoServiceImpl.findVideoById(videoId, userService.getUserId(authentication)));
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Video> saveVideo(
            @RequestPart @Validated Video video,
            @RequestPart MultipartFile videoFile,
            Authentication authentication) throws IOException {
        return ResponseEntity
                .ok()
                .body(videoServiceImpl.saveVideo(video, videoFile, userService.getUserId(authentication)));
    }

    @PatchMapping("/{videoId}")
    public ResponseEntity<Video> updateVideoById(
            @PathVariable String videoId,
            @RequestBody @Validated Video video,
            Authentication authentication) {
        return ResponseEntity
                .ok()
                .body(videoServiceImpl.updateVideoById(videoId, video, userService.getUserId(authentication)));
    }

    @DeleteMapping("/{videoId}")
    public ResponseEntity<Video> deleteVideoById(@PathVariable String videoId, Authentication authentication) {
        return ResponseEntity
                .ok()
                .body(videoServiceImpl.deleteVideoById(videoId, userService.getUserId(authentication)));
    }
}