package com.online.gallery.controller.impl;

import com.online.gallery.controller.ImageController;
import com.online.gallery.model.Image;
import com.online.gallery.service.ImageService;
import com.online.gallery.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/images")
public class ImageControllerImpl implements ImageController {
    private final ImageService imageService;
    private final UserService userService;

    public ImageControllerImpl(ImageService imageService, UserService userService) {
        this.imageService = imageService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Image>> findAllImages(Authentication authentication) {
        return ResponseEntity.ok().body(imageService.findAllImages(userService.createUserAndReturnUserId(authentication)));
    }

    @GetMapping(value = "/{imageId}", produces = {
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_GIF_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            "image/tiff",
            "image/bmp",
            "image/jpg",
            "image/webp"})
    public ResponseEntity<byte[]> findImageById(
            @PathVariable String imageId,
            Authentication authentication) {
        return ResponseEntity.ok().body(imageService.findImageById(imageId, userService.createUserAndReturnUserId(authentication)));
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Image> saveImage(
            @RequestPart @Validated Image image,
            @RequestPart MultipartFile imageFile,
            Authentication authentication) throws IOException {
        return ResponseEntity.ok().body(imageService.saveImage(imageFile, image, userService.createUserAndReturnUserId(authentication)));
    }

    @PatchMapping("/{imageId}")
    public ResponseEntity<Image> updateImageById(
            @PathVariable String imageId,
            @RequestBody @Validated Image image,
            Authentication authentication) {
        return ResponseEntity.ok().body(imageService.updateImageById(imageId, image, userService.createUserAndReturnUserId(authentication)));
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Image> deleteImageById(
            @PathVariable String imageId,
            Authentication authentication) {
        return ResponseEntity.ok().body(imageService.deleteImageById(imageId, userService.createUserAndReturnUserId(authentication)));
    }
}
