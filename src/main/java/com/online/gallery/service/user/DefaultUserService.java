package com.online.gallery.service.user;

import com.online.gallery.dto.request.ImageFileUploadRequest;
import com.online.gallery.dto.response.PresignedLinkResponse;
import com.online.gallery.entity.media.Image;
import com.online.gallery.entity.user.User;
import com.online.gallery.exception.media.image.ImageNotFoundException;
import com.online.gallery.exception.user.PasswordsMatchException;
import com.online.gallery.exception.user.UserNotEnabledException;
import com.online.gallery.exception.user.UserNotFoundException;
import com.online.gallery.exception.user.WrongPasswordException;
import com.online.gallery.repository.user.UserRepo;
import com.online.gallery.storage.s3.service.S3service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class DefaultUserService implements UserService {
    private final S3service s3service;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("${aws.s3.buckets.main-bucket}")
    private String bucketName;

    @Value("${aws.s3.image.get.expiration.s}")
    private long profileImageGetExpiration;

    @Value("${aws.s3.image.put.expiration.s}")
    private long profileImagePutExpiration;

    public DefaultUserService(S3service s3service, UserRepo userRepo,
                              PasswordEncoder passwordEncoder) {
        this.s3service = s3service;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public String generateLinkWithUserIdForS3ProfileImages(String userId) {
        return "profileImages/" + userId + "/";
    }

    @Cacheable(cacheNames = "userProfileImageCache", key = "'userId-' + #authentication")
    public String getUserId(User user) {
        User userFromDb = userRepo.findById(user.getId())
                .orElseThrow(() -> new UserNotFoundException("user not found"));
        if (!userFromDb.isEnabled()) {
            throw new UserNotEnabledException("please confirm sign up with email");
        }
        return userFromDb.getId();
    }

    @Cacheable(cacheNames = "userProfileImageCache", key = "'user-' + #authentication")
    public User getUser(User user) {
        if (!user.isEnabled()) {
            throw new UserNotEnabledException("please confirm sign up with email");
        }
        return userRepo.findById(user.getId())
                .orElseThrow(() -> new UserNotFoundException("user not found"));
    }

    public PresignedLinkResponse generatePresignedGetUrl(User user) {
        String profileImageName = user.getProfileImageName();
        if (profileImageName == null) {
            throw new ImageNotFoundException("profile image not found");
        }
        return new PresignedLinkResponse(profileImageGetExpiration,
                s3service.generatePresignedGetObjectUrl(
                        bucketName,
                        generateLinkWithUserIdForS3ProfileImages(
                                user.getId()) + profileImageName,
                        profileImageGetExpiration));
    }

    public PresignedLinkResponse generatePresignedPutUrl(
            ImageFileUploadRequest imageFileUploadRequest, User user) {
        user.setProfileImageName(imageFileUploadRequest.getName());
        userRepo.save(user);
        return new PresignedLinkResponse(profileImagePutExpiration,
                s3service.generatePresignedPutObjectUrl(
                        bucketName,
                        generateLinkWithUserIdForS3ProfileImages(user.getId()) +
                                imageFileUploadRequest.getName(),
                        profileImagePutExpiration));
    }

    @CacheEvict(cacheNames = "userProfileImageCache", allEntries = true)
    public User saveProfileImage(Image profileImageToSave, User user) {
        user.setProfileImageName(profileImageToSave.getName());

        return userRepo.save(user);
    }

    @CacheEvict(cacheNames = "userProfileImageCache", allEntries = true)
    public String updateUserPassword(String oldPassword, String newPassword, User user) {
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new WrongPasswordException("wrong old password");
        }
        if (newPassword.contentEquals(oldPassword)) {
            throw new PasswordsMatchException("both passwords match");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        return newPassword;
    }
}

