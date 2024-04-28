package com.online.gallery.service.media.image;

import com.online.gallery.dto.request.ImageDetailsRequest;
import com.online.gallery.dto.request.ImageFileUploadRequest;
import com.online.gallery.dto.response.PresignedLinkResponse;
import com.online.gallery.entity.media.Image;
import com.online.gallery.exception.media.image.ImageDuplicationException;
import com.online.gallery.exception.media.image.ImageNotFoundException;
import com.online.gallery.exception.media.video.VideoNotFoundException;
import com.online.gallery.exception.user.UserAccessDeniedException;
import com.online.gallery.repository.media.ImageRepo;
import com.online.gallery.storage.s3.service.S3service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class DefaultImageService implements ImageService {
    private final ImageRepo imageRepo;
    private final S3service s3service;
    private final String notFoundMessage = "image with this ID not found";
    private final String duplicationMessage = "image with this name is already defined";

    @Value("${aws.s3.buckets.main-bucket}")
    private String bucketName;

    @Value("${aws.s3.image.get.expiration.s}")
    private long imageGetExpiration;
    @Value("${aws.s3.image.put.expiration.s}")
    private long imagePutExpiration;

    public DefaultImageService(ImageRepo imageRepo, S3service s3service) {
        this.imageRepo = imageRepo;
        this.s3service = s3service;
    }

    private String generateLinkWithUserIdForS3Images(String imageId, String userId) {
        return "images/" + userId + "/" + imageId + "/";
    }

    @Cacheable(cacheNames = "imagesCache", key = "'imageList-' + #userId")
    public List<Image> findAllImages(String userId) {
        List<Image> allImages = imageRepo.findAllByUserId(userId);
        if (allImages.isEmpty()) {
            throw new ImageNotFoundException("images not found");
        }
        return allImages;
    }

    @Cacheable(cacheNames = "imagesCache", key = "'image-' + #imageId")
    public Image findImageById(String imageId, String userId) {
        return imageRepo.findByIdAndUserId(imageId, userId)
                .orElseThrow(() -> new ImageNotFoundException(notFoundMessage));
    }

    public PresignedLinkResponse generatePresignedGetImageUrl(String imageId, String userId) {
        Image image = imageRepo.findByIdAndUserId(imageId, userId)
                .orElseThrow(() -> new ImageNotFoundException(notFoundMessage));
        return new PresignedLinkResponse(
                imageGetExpiration,
                s3service.generatePresignedGetObjectUrl(
                        bucketName, image.getFilePath(), imageGetExpiration));
    }

    public PresignedLinkResponse generatePresignedPutUrl(
            ImageFileUploadRequest imageFileUploadRequest, String userId) {
        String imageName = imageFileUploadRequest.getName();
        String imageId = imageFileUploadRequest.getId();
        if (imageRepo.existsByIdAndUserId(imageId, userId)) {
            throw new ImageDuplicationException(duplicationMessage);
        }
        String filePath = generateLinkWithUserIdForS3Images(userId, imageId) + imageName;
        return new PresignedLinkResponse(imagePutExpiration,
                s3service.generatePresignedPutObjectUrl(
                        bucketName, filePath, imagePutExpiration));
    }

    @CacheEvict(cacheNames = "imagesCache", key = "'imageList-' + #userId")
    public Image saveImage(Image imageToSave, String userId) {
        String imageName = imageToSave.getName();
        String imageId = imageToSave.getId();
        if (imageRepo.existsByIdAndUserId(imageId, userId)) {
            throw new ImageDuplicationException(duplicationMessage);
        }
        String filePath = generateLinkWithUserIdForS3Images(userId, imageId) + imageName;
        Image image = new Image(imageId, imageName, filePath, userId);
        return imageRepo.save(image);
    }

    @CacheEvict(cacheNames = "imagesCache", allEntries = true)
    public Image updateImageById(Image newImage, String imageId, String userId) {
        if (newImage.getUserId() != null && !newImage.getUserId().equals(userId)) {
            throw new UserAccessDeniedException("you don't have access to another user");
        }
        Image imageToUpdate = imageRepo.findByIdAndUserId(imageId, userId)
                .orElseThrow(() -> new ImageNotFoundException(notFoundMessage));
        if (!imageId.equals(newImage.getId())) {
            throw new VideoNotFoundException("image with this ID in request body not found");
        }
        if (imageRepo.findByNameAndUserId(newImage.getName(), userId).isPresent()) {
            throw new ImageDuplicationException(duplicationMessage);
        }
        imageRepo.delete(imageToUpdate);
        return imageRepo.save(
                newImage);
    }

    @CacheEvict(cacheNames = "imagesCache", allEntries = true)
    public Image updateImageDetailsById(ImageDetailsRequest newImageDetails, String imageId, String userId) {
        Image imageToUpdate = imageRepo.findByIdAndUserId(imageId, userId)
                .orElseThrow(() -> new ImageNotFoundException(notFoundMessage));
        String imageName = newImageDetails.getName();
        if (imageRepo.findByNameAndUserId(imageName, userId).isPresent()) {
            throw new ImageDuplicationException(duplicationMessage);
        }
        imageToUpdate.setName(imageName);
        return imageRepo.save(
                imageToUpdate);
    }

    @CacheEvict(cacheNames = "imagesCache", allEntries = true)
    public Image deleteImageById(String imageId, String userId) {
        Image imageToDelete = imageRepo.findByIdAndUserId(imageId, userId)
                .orElseThrow(() -> new ImageNotFoundException(notFoundMessage));
        s3service.deleteObject(bucketName, imageToDelete.getFilePath());
        imageRepo.delete(imageToDelete);
        return imageToDelete;
    }
}
