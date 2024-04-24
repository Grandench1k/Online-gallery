package com.online.gallery.controller.media.image;

import com.online.gallery.dto.response.DataResponse;
import com.online.gallery.model.media.Image;
import com.online.gallery.service.media.image.ImageService;
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
@RequestMapping("api/v1/images")
public class DefaultImageController implements ImageController {
    private final ImageService imageService;
    private final UserService userService;

    public DefaultImageController(ImageService imageService, UserService userService) {
        this.imageService = imageService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<DataResponse<List<Image>>> listAllImages(Authentication authentication) {
        return ResponseEntity
                .ok(new DataResponse<>(imageService.findAllImages(userService.getUserId(authentication))));
    }

    @GetMapping(value = "/{imageId}",
            produces = {
                    MediaType.IMAGE_JPEG_VALUE,
                    MediaType.IMAGE_GIF_VALUE,
                    MediaType.IMAGE_PNG_VALUE,
                    "image/tiff",
                    "image/bmp",
                    "image/jpg",
                    "image/webp"})
    public ResponseEntity<DataResponse<byte[]>> getImageById(
            @PathVariable String imageId,
            Authentication authentication) {
        return ResponseEntity
                .ok(new DataResponse<>(imageService.findImageById(imageId, userService.getUserId(authentication))));
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<DataResponse<Image>> saveImage(
            @RequestPart @Valid Image image,
            @RequestPart MultipartFile imageFile,
            Authentication authentication) throws IOException {
        return ResponseEntity
                .ok(new DataResponse<>(imageService.saveImage(imageFile, image, userService.getUserId(authentication))));
    }

    @PatchMapping("/{imageId}")
    public ResponseEntity<DataResponse<Image>> updateImageById(
            @PathVariable String imageId,
            @RequestBody @Valid Image image,
            Authentication authentication) {
        return ResponseEntity
                .ok(new DataResponse<>(imageService.updateImageById(imageId, image, userService.getUserId(authentication))));
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<DataResponse<Image>> deleteImageById(
            @PathVariable String imageId,
            Authentication authentication) {
        return ResponseEntity
                .ok(new DataResponse<>(imageService.deleteImageById(imageId, userService.getUserId(authentication))));
    }
}
