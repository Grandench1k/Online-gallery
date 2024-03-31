package com.online.gallery.service.impl;

import com.online.gallery.exception.*;
import com.online.gallery.model.User;
import com.online.gallery.repository.UserRepository;
import com.online.gallery.service.UserService;
import com.online.gallery.storage.s3.S3service;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final S3service s3service;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${aws.s3.buckets.main-bucket}")
    private String bucketName;

    public String generateLinkWithUserIdForS3ProfileImages(String userId) {
        return "profileImages/" + userId + "/";
    }

    public String createUserAndReturnUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        String userId = user.getId();
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("user not found.");
        }
        user.checkIfUserEnabled();
        return userId;
    }

    public User createUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (!userRepository.existsById(user.getId())) {
            throw new UserNotFoundException("user not found.");
        }
        user.checkIfUserEnabled();
        return user;
    }

    public byte[] getProfileImage(User user) {
        String profileImageId = user.getProfileImageId();
        if (profileImageId == null) {
            throw new ImageNotFoundException("not found any profile images.");
        } else {
            return s3service.getObject(bucketName, generateLinkWithUserIdForS3ProfileImages(user.getId()) + profileImageId);
        }
    }

    public String saveProfileImage(MultipartFile profileImageFile, User user) throws IOException {
        String originalFilename = profileImageFile.getOriginalFilename();
        String fileFormat = Objects.requireNonNull(originalFilename).substring(originalFilename.indexOf("."));
        if (!fileFormat.contentEquals(".jpg")
                && !fileFormat.contentEquals(".png")
                && !fileFormat.contentEquals(".webp")
                && !fileFormat.contentEquals(".jpeg")) {
            throw new InvalidFilenameException("incorrect photo format, please send photo with .jpg, .png, .webp and .jpeg formats.");
        }
        String id = new ObjectId().toString();
        s3service.putObject(bucketName, generateLinkWithUserIdForS3ProfileImages(user.getId()) + id, profileImageFile.getBytes());
        user.setProfileImageId(id);
        userRepository.save(user);
        return "profile image saved.";
    }

    public String updateProfileImage(MultipartFile profileImageFile, User user) throws IOException {
        String profileImageId = user.getProfileImageId();
        if (profileImageId == null) {
            throw new ImageNotFoundException("not found any profile images.");
        }
        String id = new ObjectId().toString();
        String userId = user.getId();
        s3service.deleteObject(bucketName, generateLinkWithUserIdForS3ProfileImages(userId) + profileImageId);
        s3service.putObject(bucketName, generateLinkWithUserIdForS3ProfileImages(userId) + id, profileImageFile.getBytes());
        user.setProfileImageId(id);
        userRepository.save(user);
        return "profile image updated.";
    }

    public String updateUserPassword(String oldPassword, String newPassword, User user) {
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new WrongPasswordException("Invalid old password.");
        }
        if (newPassword.contentEquals(oldPassword)) {
            throw new PasswordsMatchException("passwords match.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return "password updated.";
    }
}
