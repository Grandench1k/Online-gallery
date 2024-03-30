package com.online.gallery.service;

import org.springframework.web.multipart.MultipartFile;
import com.online.gallery.model.Image;

import java.io.IOException;
import java.util.List;

public interface ImageService {
    String generateLinkWithUserIdForS3Images(String userId);

    List<Image> findAllImages(String userId);

    byte[] findImageById(String id, String userId);

    Image saveImage(MultipartFile imageFile, Image image, String userId) throws IOException;

    Image updateImageById(String id, Image image, String userId);

    Image deleteImageById(String id, String userId);
}
