package com.online.gallery.controller.media.image;

import com.online.gallery.dto.request.ImageDetailsRequest;
import com.online.gallery.dto.request.ImageFileUploadRequest;
import com.online.gallery.dto.response.DataResponse;
import com.online.gallery.dto.response.PresignedLinkResponse;
import com.online.gallery.entity.media.Image;
import com.online.gallery.entity.user.User;
import com.online.gallery.service.media.image.ImageService;
import com.online.gallery.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/images")
public class DefaultImageController implements ImageController {
    private final ImageService imageService;
    private final UserService userService;

    public DefaultImageController(ImageService imageService, UserService userService) {
        this.imageService = imageService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<DataResponse<List<Image>>> listAllImages(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(new DataResponse<>(
                imageService.findAllImages(userService.getUserId(user))));
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<DataResponse<Image>> getImageById(
            @PathVariable String imageId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(new DataResponse<>(
                imageService.findImageById(imageId, userService.getUserId(user))));
    }

    @GetMapping("/generate-get-url/{imageId}")
    public ResponseEntity<PresignedLinkResponse> generatePresignedGetImageUrl(
            @PathVariable String imageId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(imageService.generatePresignedGetImageUrl(
                imageId, userService.getUserId(user)));
    }

    @PostMapping("/generate-upload-url")
    public ResponseEntity<PresignedLinkResponse> prepareImageUpload(
            @RequestBody @Valid ImageFileUploadRequest imageFileUploadRequest,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(imageService.generatePresignedPutUrl(
                imageFileUploadRequest, userService.getUserId(user)));
    }

    @PostMapping()
    public ResponseEntity<DataResponse<Image>> saveImage(
            @RequestBody @Valid Image image,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(new DataResponse<>(
                imageService.saveImage(image, userService.getUserId(user))));
    }

    @PutMapping("/{imageId}")
    public ResponseEntity<DataResponse<Image>> updateImageById(
            @RequestBody @Valid Image image,
            @PathVariable String imageId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(new DataResponse<>(
                imageService.updateImageById(image,
                        imageId,
                        userService.getUserId(user))));
    }

    @PatchMapping("/{imageId}")
    public ResponseEntity<DataResponse<Image>> updateImageDetailsById(
            @RequestBody @Valid ImageDetailsRequest imageDetailsRequest,
            @PathVariable String imageId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(new DataResponse<>(
                imageService.updateImageDetailsById(
                        imageDetailsRequest,
                        imageId,
                        userService.getUserId(user))));
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<DataResponse<Image>> deleteImageById(
            @PathVariable String imageId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(new DataResponse<>(
                imageService.deleteImageById(imageId,
                        userService.getUserId(user))));
    }
}

