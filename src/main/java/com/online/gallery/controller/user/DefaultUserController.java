package com.online.gallery.controller.user;

import com.online.gallery.dto.request.ImageFileUploadRequest;
import com.online.gallery.dto.request.PasswordUpdateRequest;
import com.online.gallery.dto.response.DataResponse;
import com.online.gallery.dto.response.MessageResponse;
import com.online.gallery.dto.response.PresignedLinkResponse;
import com.online.gallery.entity.media.Image;
import com.online.gallery.entity.user.User;
import com.online.gallery.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/v1/users")
public class DefaultUserController implements UserController {
    private final UserService userService;

    public DefaultUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<DataResponse<User>> getUser(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(new DataResponse<>(userService.getUser(user)));
    }

    @GetMapping(value = "/profileImage/generate-get-url")
    public ResponseEntity<PresignedLinkResponse> getProfileImage(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                userService.generatePresignedGetUrl(userService.getUser(user)));
    }

    @PostMapping(value = "/profileImage/generate-upload-url")
    public ResponseEntity<PresignedLinkResponse> prepareProfileImageUpload(
            @RequestBody @Valid ImageFileUploadRequest imageFileUploadRequest,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                userService.generatePresignedPutUrl(
                        imageFileUploadRequest,
                        userService.getUser(user)));
    }

    @PostMapping(value = "/profileImage")
    public ResponseEntity<DataResponse<User>> saveProfileImage(
            @RequestBody @Valid Image image,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(new DataResponse<>(userService.saveProfileImage(
                image,
                userService.getUser(user))));
    }

    @PutMapping(value = "/password")
    public ResponseEntity<MessageResponse> updatePassword(
            @RequestBody @Valid PasswordUpdateRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(new MessageResponse(
                userService.updateUserPassword(
                        request.getOldPassword(),
                        request.getNewPassword(),
                        userService.getUser(user))));
    }
}
