package com.online.gallery.service.media.image;

import com.online.gallery.dto.request.ImageDetailsRequest;
import com.online.gallery.dto.request.ImageFileUploadRequest;
import com.online.gallery.dto.response.PresignedLinkResponse;
import com.online.gallery.entity.media.Image;

import java.util.List;

public interface ImageService {

    List<Image> findAllImages(String userId);

    Image findImageById(String imageId, String userId);

    PresignedLinkResponse generatePresignedGetImageUrl(String imageId, String userId);

    PresignedLinkResponse generatePresignedPutUrl(
            ImageFileUploadRequest imageFileUploadRequest,
            String userId);

    Image saveImage(Image imageToSave, String userId);

    Image updateImageById(Image newImage, String imageId, String userId);

    Image updateImageDetailsById(ImageDetailsRequest imageDetailsRequest, String imageId, String userId);

    Image deleteImageById(String imageId, String userId);
}