package com.online.gallery.service.user;

import com.online.gallery.exception.file.InvalidFileFormatException;
import com.online.gallery.exception.file.InvalidFilenameException;
import com.online.gallery.exception.media.image.ImageNotFoundException;
import com.online.gallery.exception.user.PasswordsMatchException;
import com.online.gallery.exception.user.UserNotFoundException;
import com.online.gallery.exception.user.WrongPasswordException;
import com.online.gallery.model.user.User;
import com.online.gallery.repository.user.UserRepository;
import com.online.gallery.storage.s3.service.S3service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Transactional
@Service
public class DefaultUserService implements UserService {
    private final S3service s3service;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${aws.s3.buckets.main-bucket}")
    private String bucketName;

    public DefaultUserService(S3service s3service, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.s3service = s3service;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private String generateLinkWithUserIdForS3ProfileImages(String userId) {
        return "profileImages/" + userId + "/";
    }

    public String getUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        String userId = user.getId();
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("user not found");
        }
        user.checkIfUserEnabled();
        return userId;
    }

    public User getUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userRepository.findById(user.getId())
                .orElseThrow(() -> new UserNotFoundException("user not found"));
    }

    @Cacheable(cacheNames = "userProfileImageCache", key = "#user.id")
    public byte[] getProfileImage(User user) {
        String profileImageId = user.getProfileImageName();
        if (profileImageId == null) {
            throw new ImageNotFoundException("profile image not found");
        }
        return s3service.getObject(bucketName, generateLinkWithUserIdForS3ProfileImages(user.getId()) + profileImageId);
    }

    private String getFileFormat(MultipartFile imageFile) {
        String originalFilename = imageFile.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new InvalidFilenameException("invalid filename");
        }
        String fileFormat = originalFilename.substring(originalFilename.lastIndexOf("."));
        Set<String> allowedFormats = new HashSet<>(Arrays.asList(
                ".jpg", ".png", ".webp", ".jpeg"));
        if (!allowedFormats.contains(fileFormat)) {
            throw new InvalidFileFormatException("incorrect photo format, please send photo" +
                    " with .jpg, .png, .webp or .jpeg formats");
        }
        return fileFormat;
    }

    @CachePut(cacheNames = "userProfileImageCache", key = "#user.id")
    public String saveProfileImage(MultipartFile profileImageFile, User user) throws IOException {
        String fileFormat = getFileFormat(profileImageFile);
        String id = new ObjectId().toString();
        String nameOfNewFile = id + "_" + fileFormat;
        s3service.putObject(bucketName,
                generateLinkWithUserIdForS3ProfileImages(user.getId()) + nameOfNewFile,
                profileImageFile.getBytes());
        user.setProfileImageName(id);
        userRepository.save(user);
        return "profile image saved";
    }

    @CacheEvict(cacheNames = "userProfileImageCache", key = "#user.id")
    public String updateProfileImage(MultipartFile profileImageFile, User user) throws IOException {
        String profileImageId = user.getProfileImageName();
        if (profileImageId == null) {
            throw new ImageNotFoundException("profile image with this ID not found");
        }
        String id = new ObjectId().toString();
        String userId = user.getId();
        s3service.deleteObject(bucketName,
                generateLinkWithUserIdForS3ProfileImages(userId) + profileImageId);
        s3service.putObject(bucketName,
                generateLinkWithUserIdForS3ProfileImages(userId) + id,
                profileImageFile.getBytes());
        user.setProfileImageName(id);
        userRepository.save(user);
        return "profile image updated";
    }

    @CachePut(cacheNames = "userProfileCache", key = "#user.username")
    public String updateUserPassword(String oldPassword, String newPassword, User user) {
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new WrongPasswordException("wrong old password");
        }
        if (newPassword.contentEquals(oldPassword)) {
            throw new PasswordsMatchException("both passwords match");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return "password updated";
    }
}
