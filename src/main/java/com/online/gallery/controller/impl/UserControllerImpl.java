package com.online.gallery.controller.impl;

import com.online.gallery.controller.UserController;
import com.online.gallery.dto.request.PasswordUpdateRequest;
import com.online.gallery.dto.response.OkResponse;
import com.online.gallery.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "api/v1/user")
public class UserControllerImpl implements UserController {
    private final UserService userService;

    @GetMapping(value = "/profileImage", produces = {
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_GIF_VALUE,
            MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<byte[]> getUserProfileImage(Authentication authentication) {
        return ResponseEntity.ok().body(userService.getProfileImage(userService.createUser(authentication)));
    }

    @PostMapping(value = "/profileImage/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OkResponse> saveUserProfileImage(
            @RequestPart MultipartFile profileImageFile,
            Authentication authentication) throws IOException {
        return ResponseEntity.ok().body(new OkResponse(
                userService.saveProfileImage(
                        profileImageFile,
                        userService.createUser(authentication))));
    }

    @PatchMapping(value = "/profileImage/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OkResponse> updateUserProfileImage(
            @RequestPart MultipartFile profileImageFile,
            Authentication authentication) throws IOException {
        return ResponseEntity.ok().body(new OkResponse(userService.updateProfileImage(profileImageFile, userService.createUser(authentication))));
    }

    @PatchMapping(value = "/password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OkResponse> updateUserPassword(
            @RequestBody @Validated PasswordUpdateRequest passwordUpdateRequest,
            Authentication authentication) {
        return ResponseEntity.ok().body(new OkResponse(
                userService.updateUserPassword(
                        passwordUpdateRequest.getOldPassword(),
                        passwordUpdateRequest.getNewPassword(),
                        userService.createUser(authentication))));
    }
}
