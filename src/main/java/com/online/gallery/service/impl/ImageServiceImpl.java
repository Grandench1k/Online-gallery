package com.online.gallery.service.impl;

import com.online.gallery.exception.ImageDuplicationException;
import com.online.gallery.exception.ImageNotFoundException;
import com.online.gallery.exception.InvalidFileFormatException;
import com.online.gallery.model.Image;
import com.online.gallery.repository.ImageRepository;
import com.online.gallery.service.ImageService;
import com.online.gallery.storage.s3.S3service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;
    private final S3service s3service;
    @Value("${aws.s3.buckets.main-bucket}")
    private String bucketName;

    public ImageServiceImpl(ImageRepository imageRepository, S3service s3service) {
        this.imageRepository = imageRepository;
        this.s3service = s3service;
    }

    public String generateLinkWithUserIdForS3Images(String userId) {
        return "images/" + userId + "/";
    }

    public List<Image> findAllImages(String userId) {
        List<Image> allImages = imageRepository.findAllByUserId(userId);
        if (allImages.isEmpty()) {
            throw new ImageNotFoundException("images not found.");
        }
        return allImages;
    }

    @Cacheable(cacheNames = "imageCache", key = "#id")
    public byte[] findImageById(String id, String userId) {
        Image image = imageRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ImageNotFoundException("image with this id not found."));
        return s3service.getObject(bucketName, generateLinkWithUserIdForS3Images(userId) + image.getUri());
    }

    @CachePut(cacheNames = "imageCache")
    public Image saveImage(MultipartFile imageFile, Image image, String userId) throws IOException {
        if (imageRepository.findByNameAndUserId(image.getName(), userId).isPresent()) {
            throw new ImageDuplicationException("image with this name is already defined.");
        }
        String fileFormat = checkAndGetFileFormat(imageFile);
        String id = new ObjectId().toString();
        String nameOfNewFile = id + fileFormat;
        s3service.putObject(bucketName, generateLinkWithUserIdForS3Images(userId) + nameOfNewFile, imageFile.getBytes());
        image.setUri(nameOfNewFile);
        image.setId(id);
        image.setUserId(userId);
        imageRepository.save(image);
        return image;
    }

    private String checkAndGetFileFormat(MultipartFile imageFile) {
        String originalFilename = imageFile.getOriginalFilename();
        String fileFormat = Objects.requireNonNull(originalFilename).substring(originalFilename.indexOf("."));
        if (!fileFormat.contentEquals(".jpg")
                && !fileFormat.contentEquals(".png")
                && !fileFormat.contentEquals(".webp")
                && !fileFormat.contentEquals(".bmp")
                && !fileFormat.contentEquals(".jpeg")
                && !fileFormat.contentEquals(".gif")
                && !fileFormat.contentEquals(".tiff")) {
            throw new InvalidFileFormatException("incorrect image format, please send image with .jpg, .jpeg, .png, .webp, .bmp, .gif and .tiff formats.");
        }
        return fileFormat;
    }


    @CachePut(cacheNames = "imageCache", key = "#id")
    public Image updateImageById(String id, Image image, String userId) {
        Image imageToUpdate = imageRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ImageNotFoundException("image with this id not found."));
        String imageName = image.getName();
        if (imageRepository.findByNameAndUserId(imageName, userId).isPresent()) {
            throw new ImageDuplicationException("image with this name is already defined.");
        }
        imageToUpdate.setName(imageName);
        imageRepository.save(imageToUpdate);
        return imageToUpdate;
    }

    @CacheEvict(cacheNames = "imageCache", key = "#id")
    public Image deleteImageById(String id, String userId) {
        Image imageToDelete = imageRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ImageNotFoundException("image with this id not found."));
        s3service.deleteObject(bucketName, "images/" + imageToDelete.getUri());
        imageRepository.delete(imageToDelete);
        return imageToDelete;
    }
}
