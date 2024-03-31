package com.online.gallery.controller.impl;

import com.online.gallery.controller.VideoController;
import com.online.gallery.model.Video;
import com.online.gallery.service.UserService;
import com.online.gallery.service.impl.VideoServiceImpl;
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
public class VideoControllerImpl implements VideoController {
    private final VideoServiceImpl videoServiceImpl;
    private final UserService userService;

    public VideoControllerImpl(VideoServiceImpl videoServiceImpl, UserService userService) {
        this.videoServiceImpl = videoServiceImpl;
        this.userService = userService;
    }


    @GetMapping
    public ResponseEntity<List<Video>> findAllVideos(Authentication authentication) {
        return ResponseEntity.ok().body(videoServiceImpl.findAllVideos(userService.createUserAndReturnUserId(authentication)));
    }

    @GetMapping(value = "/{videoId}", produces = {"video/mp4", "video/mpeg", "video/ogg"})
    public ResponseEntity<byte[]> findVideoById(@PathVariable String videoId, Authentication authentication) {
        return ResponseEntity.ok().contentType(MediaType.valueOf("video/mp4")).body(videoServiceImpl.findVideoById(videoId, userService.createUserAndReturnUserId(authentication)));
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Video> saveVideo(@RequestPart @Validated Video video, @RequestPart MultipartFile videoFile, Authentication authentication) throws IOException {
        return ResponseEntity.ok().body(videoServiceImpl.saveVideo(video, videoFile, userService.createUserAndReturnUserId(authentication)));
    }

    @PatchMapping("/{videoId}")
    public ResponseEntity<Video> updateVideoById(@PathVariable String videoId, @RequestBody @Validated Video video, Authentication authentication) {
        return ResponseEntity.ok().body(videoServiceImpl.updateVideoById(videoId, video, userService.createUserAndReturnUserId(authentication)));
    }

    @DeleteMapping("/{videoId}")
    public ResponseEntity<Video> deleteVideoById(@PathVariable String videoId, Authentication authentication) {
        return ResponseEntity.ok().body(videoServiceImpl.deleteVideoById(videoId, userService.createUserAndReturnUserId(authentication)));
    }
}
