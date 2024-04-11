package com.online.gallery.service.media.image;

import com.online.gallery.model.media.Image;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {
    String generateLinkWithUserIdForS3Images(String userId);

    List<Image> findAllImages(String userId);

    byte[] findImageById(String imageId, String userId);

    void checkFileFormat(String originalFilename);

    Image saveImage(MultipartFile imageFile, Image image, String userId) throws IOException;

    Image updateImageById(String imageId, Image image, String userId);

    Image deleteImageById(String imageId, String userId);
}
