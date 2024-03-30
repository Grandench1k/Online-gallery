package com.online.gallery.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.online.gallery.controller.VideoController;
import com.online.gallery.model.Video;
import com.online.gallery.service.UserService;
import com.online.gallery.service.impl.VideoServiceImpl;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/video")
public class VideoControllerImpl implements VideoController {
    private final VideoServiceImpl videoServiceImpl;
    private final UserService userService;


    @GetMapping
    public ResponseEntity<List<Video>> findAllVideos(Authentication authentication) {
        return ResponseEntity.ok().body(videoServiceImpl.findAllVideos(userService.createUserAndReturnUserId(authentication)));
    }

    @GetMapping(value = "/{id}", produces = {"video/mp4", "video/mpeg", "video/ogg"})
    public ResponseEntity<byte[]> findVideoById(@PathVariable String id, Authentication authentication) {
        return ResponseEntity.ok().contentType(MediaType.valueOf("video/mp4")).body(videoServiceImpl.findVideoById(id, userService.createUserAndReturnUserId(authentication)));
    }

    @PostMapping(value = "/save", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Video> saveVideo(@RequestPart @Validated Video video, @RequestPart MultipartFile videoFile, Authentication authentication) throws IOException {
        return ResponseEntity.ok().body(videoServiceImpl.saveVideo(video, videoFile, userService.createUserAndReturnUserId(authentication)));
    }

    @PatchMapping("/{id}/update")
    public ResponseEntity<Video> updateVideoById(@PathVariable String id, @RequestBody @Validated Video video, Authentication authentication) {
        return ResponseEntity.ok().body(videoServiceImpl.updateVideoById(id, video, userService.createUserAndReturnUserId(authentication)));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Video> deleteVideoById(@PathVariable String id, Authentication authentication) {
        return ResponseEntity.ok().body(videoServiceImpl.deleteVideoById(id, userService.createUserAndReturnUserId(authentication)));
    }
}
