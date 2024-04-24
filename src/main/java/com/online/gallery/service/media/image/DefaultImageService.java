package com.online.gallery.service.media.image;

import com.online.gallery.exception.file.InvalidFileFormatException;
import com.online.gallery.exception.file.InvalidFilenameException;
import com.online.gallery.exception.media.image.ImageDuplicationException;
import com.online.gallery.exception.media.image.ImageNotFoundException;
import com.online.gallery.model.media.Image;
import com.online.gallery.repository.media.ImageRepository;
import com.online.gallery.storage.s3.service.S3service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Transactional
@Service
public class DefaultImageService implements ImageService {
    private final ImageRepository imageRepository;
    private final S3service s3service;

    @Value("${aws.s3.buckets.main-bucket}")
    private String bucketName;

    public DefaultImageService(ImageRepository imageRepository, S3service s3service) {
        this.imageRepository = imageRepository;
        this.s3service = s3service;
    }

    private String generateLinkWithUserIdForS3Images(String userId) {
        return "images/" + userId + "/";
    }

    @Cacheable(cacheNames = "imageCache", key = "'list-' + #userId")
    public List<Image> findAllImages(String userId) {
        List<Image> allImages = imageRepository.findAllByUserId(userId);
        if (allImages.isEmpty()) {
            throw new ImageNotFoundException("no images found for user: " + userId);
        }
        return allImages;
    }

    public byte[] findImageById(String imageId, String userId) {
        Image image = imageRepository.findByIdAndUserId(imageId, userId)
                .orElseThrow(() -> new ImageNotFoundException("image with ID " + imageId + " not found"));
        return s3service.getObject(bucketName, generateLinkWithUserIdForS3Images(userId) + image.getFilePath());
    }

    private void checkFileFormat(String originalFilename) {
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new InvalidFilenameException("invalid filename");
        }
        String fileFormat = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        Set<String> allowedFormats = new HashSet<>(
                Arrays.asList(".jpg", ".png", ".webp", ".bmp", ".jpeg", ".gif", ".tiff"));
        if (!allowedFormats.contains(fileFormat)) {
            throw new InvalidFileFormatException(
                    "incorrect image format. Please send an image with one" +
                            " of the following formats: .jpg, .png, .webp, .bmp, .jpeg, .gif, .tiff");
        }
    }

    @CachePut(cacheNames = "imageCache", key = "'list-' + #userId")
    public Image saveImage(MultipartFile imageFile, Image image, String userId) throws IOException {
        imageRepository.findByNameAndUserId(image.getName(), userId).ifPresent(s -> {
            throw new ImageDuplicationException("image with this name is already defined for user " + userId);
        });
        String originalFilename = imageFile.getOriginalFilename();
        checkFileFormat(originalFilename);
        String id = new ObjectId().toString();
        String nameOfNewFile = id + "_" + originalFilename;
        s3service.putObject(
                bucketName,
                generateLinkWithUserIdForS3Images(userId) + nameOfNewFile,
                imageFile.getBytes());
        image.setFilePath(nameOfNewFile);
        image.setId(id);
        image.setUserId(userId);
        return imageRepository.save(image);
    }

    @CachePut(cacheNames = "imageCache", key = "'image-' + #imageId")
    public Image updateImageById(String imageId, Image newImage, String userId) {
        Image imageToUpdate = imageRepository.findByIdAndUserId(imageId, userId)
                .orElseThrow(() -> new ImageNotFoundException("image with ID " + imageId + " not found"));
        String imageName = newImage.getName();
        if (imageRepository.findByNameAndUserId(imageName, userId).isPresent()) {
            throw new ImageDuplicationException("image with this name is already defined");
        }
        imageToUpdate.setName(imageName);
        return imageRepository.save(imageToUpdate);
    }

    @CacheEvict(cacheNames = "imageCache", allEntries = true)
    public Image deleteImageById(String imageId, String userId) {
        Image imageToDelete = imageRepository.findByIdAndUserId(imageId, userId)
                .orElseThrow(() -> new ImageNotFoundException("image with ID " + imageId + " not found"));
        s3service.deleteObject(bucketName, "images/" + imageToDelete.getFilePath());
        imageRepository.delete(imageToDelete);
        return imageToDelete;
    }
}
